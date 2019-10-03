package com.user.controller;

import com.dubbo.commons.Const;
import com.dubbo.commons.ServerResponse;
import com.user.entity.Article;
import com.user.entity.Comment;
import com.user.entity.User;
import com.user.service.CommentService;
import com.user.utils.JedisUtil;
import com.user.utils.JsonUtil;
import com.user.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 实现高并发
 */
@RestController
@RequestMapping("/comment/")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 线程池threadPoolExecutor
     * corePoolSize 20
     * maximumPoolSize  50
     * timeUnit.SECONDS 10
     * BlockingQueue    LinkedBlockingQueue
     * new ThreadFactoryBuilder().setNameFormat("XX-task-%d").build()
     */
    //发表评论
    @RequestMapping(value = "insertComment",method = RequestMethod.POST)
    public ServerResponse insertComment(HttpServletRequest httpServletRequest, String userName,Comment comment){
        if (!JwtTokenUtil.checkRole(httpServletRequest,userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        comment.setCommentId(JedisUtil.getCommentId());
        comment.setCommentUserId(JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId());
        return commentService.addComment(comment);
    }

    @RequestMapping(value = "deleteComment",method = RequestMethod.POST)
    public ServerResponse deleteComment(HttpServletRequest httpServletRequest, String userName,String commentId){
        if (!JwtTokenUtil.checkRole(httpServletRequest,userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        if (!JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId().equals(JsonUtil.stringToObj(JedisUtil.getValue(Const.RedisKey.BeforeCommentKeyId+commentId),Comment.class).getCommentUserId())){
            return ServerResponse.createByErrorMessage("不要删除别人的评论");
        }
        return commentService.deleteComment(commentId);
    }

    @RequestMapping(value = "updateComment",method = RequestMethod.POST)
    public ServerResponse updateComment(HttpServletRequest httpServletRequest, String userName,String commentId,String commentContent){
        if (!JwtTokenUtil.checkRole(httpServletRequest,userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        if (!JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId().equals(JsonUtil.stringToObj(JedisUtil.getValue(Const.RedisKey.BeforeCommentKeyId+commentId),Comment.class).getCommentUserId())){
            return ServerResponse.createByErrorMessage("不要更新别人的评论");
        }
        return commentService.updateComment(commentId,commentContent);
    }


    @RequestMapping(value = "selectComment",method = RequestMethod.POST)
    public ServerResponse selectComment(HttpServletRequest httpServletRequest, String userName,String articleId){
        if (!JwtTokenUtil.checkRole(httpServletRequest,userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        //通过userName得到用户ID
        String userIdByUserName=JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,null).getUserId();
        //通过articleId得到用户ID
        String userIdByArticleId=JedisUtil.getValue(Const.RedisKey.BeforeArticleKeyId+articleId);
        if (!userIdByArticleId.equals(userIdByUserName)){
            return ServerResponse.createByErrorMessage("不要查看别人的评论");
        }
        return commentService.selectComment(articleId);
    }


}
