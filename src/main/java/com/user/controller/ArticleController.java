package com.user.controller;

import com.dubbo.ElasticsearchService;
import com.dubbo.commons.Const;
import com.dubbo.commons.ImgResultDto;
import com.dubbo.commons.ServerResponse;
import com.dubbo.entity.Article;
import com.user.service.ArticleService;
import com.user.service.CommentService;
import com.user.utils.JedisUtil;
import com.user.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/upload/")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Reference(version = "1.0.0")
    private ElasticsearchService elasticsearchService;

    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", maxAge = 3600)
    @RequestMapping(value = "uploadImage")
    public ImgResultDto uploadImage(@RequestParam("uploadImage") List<MultipartFile> list,HttpServletRequest httpServletRequest, HttpServletResponse response){
        //是否跨域的验证请求
        if(httpServletRequest.getMethod().equals("OPTIONS")){
            //允许任何请求访问
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8082");
            response.setHeader("Access-Control-Allow-Headers", "Content-Length,X-Requested-With,content-type");
            response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
            return null;
        }

        //服务曾处理数据，返回Dto
        return articleService.uploadImage(list,1);
    }

    //发表文章
    @RequestMapping(value = "uploadArticle",method = RequestMethod.POST)
    public ServerResponse uploadArticle(HttpServletRequest httpServletRequest,String userName,String articleTitle,String articleContent){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        //使用userName从redis反序列化出user，在得到userId->article_user_id
        Article article=new Article(
                JedisUtil.getArticleId(),
                JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId(),
                articleTitle,
                articleContent,
                null,
                null
        );
        //将articleId-articleUserId存入redis中  commentId-1 10
        JedisUtil.setKey(Const.RedisKey.BeforeArticleKeyId +article.getArticleId(),article.getArticleUserId());

        return elasticsearchService.addArticle(article);
    }

    //删除文章
    @RequestMapping(value = "deleteArticle",method = RequestMethod.POST)
    public ServerResponse deleteArticle(HttpServletRequest httpServletRequest,String userName,String articleId){
        //也要把redis中存的articleId-articleUserId删除
        JedisUtil.delKey(Const.RedisKey.BeforeArticleKeyId+articleId);
        //也要把评论过此文章的评论删除
        commentService.deleteCommentsForDeleteArticle(articleId);
        return elasticsearchService.deleteArticle(articleId);
    }

    //更新文章
    @RequestMapping(value = "updateArticle",method = RequestMethod.POST)
    public ServerResponse updateArticle(HttpServletRequest httpServletRequest,String userName,Article article){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        //根据文章ID查出此文章所属用户ID，比较ID是否相同
        String articleUserId=JedisUtil.getValue(Const.RedisKey.BeforeArticleKeyId+article.getArticleId());
        if (!JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId().equals(articleUserId)){
            return ServerResponse.createByErrorMessage("不要更新别人的文章");
        }
        article.setArticleUserId(JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId());
        return elasticsearchService.updateArticle(article);
    }

    //GET文章
    @RequestMapping(value = "getArticle",method = RequestMethod.POST)
    public ServerResponse getArticle(HttpServletRequest httpServletRequest,String userName,String articleId){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        //根据文章ID查出此文章所属用户ID，比较ID是否相同
        String articleUserId=JedisUtil.getValue(Const.RedisKey.BeforeArticleKeyId+articleId);
        if (!JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId().equals(articleUserId)){
            return ServerResponse.createByErrorMessage("通过这种方式只能得到自己的文章，不能得到别人的文章");
        }

        return elasticsearchService.selectArticle(articleId);
    }

    /**
     * 查询文章
     * 搜索服务
     * 前端传过来    搜索人id,搜索关键字
     */
    @RequestMapping(value = "searchArticle",method = RequestMethod.POST)
    public ServerResponse searchArticle(HttpServletRequest httpServletRequest,String userName,String userInputText,int pageNum){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        return elasticsearchService.searchArticle(userInputText,pageNum);
    }






    private Boolean checkRole(HttpServletRequest httpServletRequest,String userName){
        String token = httpServletRequest.getHeader(JwtTokenUtil.tokenHeader);
        if (!StringUtils.isEmpty(token)) {
            Claims claims = Jwts.parser()
                    .setSigningKey(JwtTokenUtil.secret)
                    .parseClaimsJws(token.replace(JwtTokenUtil.tokenPrefix, ""))
                    .getBody();
            //相等jwt中token和用户名一致
            return claims.getSubject().equals(userName);
        }
        return false;
    }
}
