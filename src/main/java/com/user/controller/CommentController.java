package com.user.controller;

import com.dubbo.commons.ServerResponse;
import com.user.entity.Comment;
import com.user.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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
    @RequestMapping(value = "insertComment",method = RequestMethod.POST)
    public ServerResponse insertComment(Comment comment){
        return commentService.addComment(comment);
    }

    @RequestMapping(value = "deleteComment",method = RequestMethod.POST)
    public ServerResponse deleteComment(String commentId){
        return commentService.deleteComment(commentId);
    }

    @RequestMapping(value = "updateComment",method = RequestMethod.POST)
    public ServerResponse updateComment(String commentId,String commentContent){
        return commentService.updateComment(commentId,commentContent);
    }
    @RequestMapping(value = "selectComment",method = RequestMethod.POST)
    public ServerResponse selectComment(String articleId){
        return commentService.selectComment(articleId);
    }


}
