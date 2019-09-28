package com.user.service.impl;

import com.dubbo.commons.ServerResponse;
import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;
import com.user.entity.Comment;
import com.user.mapper.CommentMapper;
import com.user.service.CommentService;
import com.user.utils.JedisUtil;
import com.user.utils.JsonUtil;
import com.user.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Service("commentService")
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

    //发表评论
    public ServerResponse addComment(Comment comment){
        if (comment!=null){
            int count=commentMapper.insert(comment);
            if (count>0){
                //说明存入数据库成功，接下来存入redis
                JedisUtil.setEntityToRedis(comment.getCommentId(),comment);
                return ServerResponse.createBySuccessMessage("发表评论成功");
            }else {
                return ServerResponse.createByErrorMessage("发表评论失败");
            }
        }
        return ServerResponse.createByErrorMessage("评论不能为空");
    }

    //删除评论
    public ServerResponse deleteComment(String commentId){
        if (commentId!=null){
            int count=commentMapper.deleteByPrimaryKey(commentId);
            if (count>0){
                JedisUtil.delKey(commentId);
                return ServerResponse.createBySuccessMessage("删除评论成功");
            }else {
                return ServerResponse.createByErrorMessage("删除评论失败");
            }
        }
        return ServerResponse.createByErrorMessage("评论不能为空");
    }

    //更新评论
    public ServerResponse updateComment(String commentId,String commentContent){
        //首先在redis中查看有没有这个评论
        Comment comment= JsonUtil.stringToObj(JedisUtil.getValue(commentId),Comment.class);
        if (comment!=null){
            //说明redis中有此评论，然后先更新数据库中评论，在更新redis中
            comment.setCommentContent(commentContent);
            int count=commentMapper.updateByPrimaryKey(comment);
            if (count>0){
                JedisUtil.setEntityToRedis(commentId,comment);
                return ServerResponse.createBySuccessMessage("更新评论成功");
            }
            return ServerResponse.createByErrorMessage("更新数据库失败");
        }else {
            //说明redis中没有此评论
            comment=commentMapper.selectByPrimaryKey(commentId);
            if (comment!=null){
                //说明数据库中有此评论，但redis中没有评论，是因为评论超过存活时间1周
                comment.setCommentContent(commentContent);
                int count=commentMapper.updateByPrimaryKey(comment);
                if (count>0){
                    JedisUtil.setEntityToRedis(commentId,comment);
                    return ServerResponse.createBySuccessMessage("更新评论成功");
                }
            }
            return ServerResponse.createByErrorMessage("没有此评论");
        }

    }


    /**
     *     查看评论
     *     一般是在阅读文章时，需要查询文章的评论
     *     还有别人回复是，需要查看评论的回复（评论）
     */

    public ServerResponse selectComment(String articleId){
        //首先得到直接评论此文章的评论集合
        List<Comment> commentList=commentMapper.selectCommentByArticleIdAndFartherId(articleId,"0");
        List<CommentVo> commentVoList=new ArrayList<>();
        for (Comment comment : commentList){
            //第一步将Comment对象转换为CommentVo对象,并且遍历其子评论
            CommentVo commentVo=new CommentVo(
                    getCommentList(comment.getCommentId()),
                    comment.getCommentId(),
                    comment.getCommentUserId(),
                    comment.getCommentArticleId(),
                    comment.getCommentFarther(),
                    comment.getCommentContent(),
                    comment.getCreateTime(),
                    comment.getUpdateTime()
            );
            commentVoList.add(commentVo);
        }
        return ServerResponse.createBySuccess(commentVoList);
    }


    //迭代查询评论
    private List<CommentVo> getCommentList(String commentId) {
        //将fartherId==commentId的查询出来
        List<Comment> commentList=commentMapper.selectCommentByFartherId(commentId);
        //如果结果不为null，说明有人在评，为null说明此评论无人在评
        if (commentList!=null){
            List<CommentVo> commentVoList=new ArrayList<>();
            for (Comment comment : commentList){
                //第一步将Comment对象转换为CommentVo对象
                CommentVo commentVo=new CommentVo(
                        getCommentList(comment.getCommentId()),
                        comment.getCommentId(),
                        comment.getCommentUserId(),
                        comment.getCommentArticleId(),
                        comment.getCommentFarther(),
                        comment.getCommentContent(),
                        comment.getCreateTime(),
                        comment.getUpdateTime()
                );
                commentVoList.add(commentVo);
            }
            return commentVoList;
        }
        return null;
    }

}
