package com.user.controller;

import com.dubbo.ElasticsearchService;
import com.dubbo.commons.ImgResultDto;
import com.dubbo.commons.ServerResponse;
import com.dubbo.entity.Article;
import com.user.service.ArticleService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Reference(version = "1.0.0")
    private ElasticsearchService elasticsearchService;

    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", methods = RequestMethod.OPTIONS, maxAge = 3600)
    @RequestMapping(value = "uploadImage")
    public ImgResultDto uploadImage(@RequestParam("uploadImage") List<MultipartFile> list, HttpServletRequest request, HttpServletResponse response){
        //这里是我在web中定义的两个初始化属性，保存目录的绝对路径和相对路径，你们可以自定义
        String imgUploadAbsolutePath = request.getServletContext().getInitParameter("imgUploadAbsolutePath");
        String imgUploadRelativePath = request.getServletContext().getInitParameter("imgUploadRelativePath");

        //允许任何请求访问
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8082");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Length,X-Requested-With,content-type");
        response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");

        //服务曾处理数据，返回Dto
        return articleService.uploadImage(list,imgUploadAbsolutePath,imgUploadRelativePath,1);
    }

    //发表文章
    @RequestMapping(value = "uploadArticle",method = RequestMethod.POST)
    public ServerResponse uploadArticle(Article article){
        return elasticsearchService.addArticle(article);
    }

    //删除文章
    @RequestMapping(value = "deleteArticle",method = RequestMethod.POST)
    public ServerResponse deleteArticle(String articleId){
        return elasticsearchService.deleteArticle(articleId);
    }

    //更新文章
    @RequestMapping(value = "updateArticle",method = RequestMethod.POST)
    public ServerResponse updateArticle(Article article){
        return elasticsearchService.updateArticle(article);
    }

    //GET文章
    @RequestMapping(value = "getArticle",method = RequestMethod.POST)
    public ServerResponse getArticle(String articleId){
        return elasticsearchService.selectArticle(articleId);
    }

    /**
     * 查询文章
     * 搜索服务
     * 前端传过来    搜索人id,搜索关键字
     */
    @RequestMapping(value = "searchArticle",method = RequestMethod.POST)
    public ServerResponse searchArticle(String userInputText,int pageNum){
        return elasticsearchService.searchArticle(userInputText,pageNum);
    }

}
