package com.user.controller;

import com.dubbo.ElasticsearchService;
import com.dubbo.commons.Const;
import com.dubbo.commons.ImgResultDto;
import com.dubbo.commons.ServerResponse;
import com.dubbo.entity.Article;
import com.dubbo.util.JsonUtil;
import com.user.entity.User;
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
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(allowedHeaders="*", allowCredentials="false", methods={RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
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
                0,
                null,
                null
        );
        //这个是方便查看此文章是否文此用户的
        JedisUtil.setKey(Const.RedisKey.BeforeArticleKeyId +article.getArticleId(),article.getArticleUserId());
        //发表文章后，将BeforeArticleKeyId+userId,articleId存入redis中,方便以后找到用户发表的文章集合
        com.dubbo.util.JedisUtil.setList(Const.RedisKey.BeforeUserKeyForArticleId+article.getArticleUserId(),article.getArticleId());
        return elasticsearchService.addArticle(article);
    }

    //删除文章
    @RequestMapping(value = "deleteArticle",method = RequestMethod.POST)
    public ServerResponse deleteArticle(HttpServletRequest httpServletRequest,String userName,String articleId){
        //也要删除redis中存的BeforeArticleKeyId+userId,articleId
        com.dubbo.util.JedisUtil.delList(Const.RedisKey.BeforeUserKeyForArticleId+ JedisUtil.getValue(articleId),articleId);
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

    //更新文章热点
    @RequestMapping(value = "updateArticleHeat",method = RequestMethod.POST)
    public ServerResponse updateArticleHeat(String articleId,Integer articleHeat){
        return elasticsearchService.updateArticleHeat(articleId,articleHeat);
    }

    //查看一片文章
    @RequestMapping(value = "getArticle",method = RequestMethod.POST)
    public ServerResponse getOwnArticle(String articleId){
        return elasticsearchService.selectArticle(articleId);
    }

    //查看自己已发表的文章
    @RequestMapping(value = "getOwnArticles",method = RequestMethod.POST)
    public ServerResponse getOwnArticles(HttpServletRequest httpServletRequest,String userName){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        String userId = JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName, null).getUserId();
        List<String> articleIdList= com.dubbo.util.JedisUtil.getList(Const.RedisKey.BeforeUserKeyForArticleId+userId);
        return elasticsearchService.getOwnArticle(articleIdList);
    }

    //首页推荐
    @RequestMapping(value = "homeArticle",method = RequestMethod.POST)
    public ServerResponse homeArticle(Integer pageNum){
        return elasticsearchService.homeArticleInteger(pageNum);
    }

    /**
     * 查询文章
     * 搜索服务
     * 前端传过来    搜索人id,搜索关键字
     */
    @RequestMapping(value = "searchArticle",method = RequestMethod.POST)
    public ServerResponse searchArticle(HttpServletRequest httpServletRequest,String userName,String userInputText,int pageNum){
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
