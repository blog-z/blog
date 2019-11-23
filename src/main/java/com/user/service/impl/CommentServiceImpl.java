package com.user.service.impl;

import com.dubbo.ElasticsearchService;
import com.dubbo.commons.Const;
import com.dubbo.commons.ServerResponse;
import com.user.entity.Comment;
import com.user.mapper.CommentMapper;
import com.user.mapper.UserMapper;
import com.user.service.CommentService;
import com.user.utils.JedisUtil;
import com.user.utils.JsonUtil;
import com.user.vo.CommentAndArticleName;
import com.user.vo.CommentVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Service("commentService")
public class CommentServiceImpl implements CommentService{

    @Reference(version = "1.0.0")
    private ElasticsearchService elasticsearchService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    //发表评论
    public ServerResponse addComment(Comment comment){
        if (comment!=null){
            int count=commentMapper.insert(comment);
            if (count>0){
                //说明存入数据库成功，接下来存入redis
                JedisUtil.setEntityToRedis(Const.RedisKey.BeforeCommentKeyId+comment.getCommentId(),comment);
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
            //删除评论也要删除它的子评论
            int count=commentMapper.deleteByPrimaryKey(commentId);
            if (count>0){
                deleteComments(commentId);
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
                JedisUtil.setEntityToRedis(Const.RedisKey.BeforeCommentKeyId+commentId,comment);
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
                    JedisUtil.setEntityToRedis(Const.RedisKey.BeforeCommentKeyId+commentId,comment);
                    return ServerResponse.createBySuccessMessage("更新评论成功");
                }
            }
            return ServerResponse.createByErrorMessage("没有此评论");
        }

    }

    public ServerResponse getOwnComments(String commentUserId){
        List<Comment> commentList=commentMapper.selectOwnCommentsByCommentUserId(commentUserId);
        List<CommentAndArticleName> commentAndArticleNameList=new ArrayList<>();
        for (Comment comment : commentList){
            CommentAndArticleName commentAndArticleName=new CommentAndArticleName(
                    elasticsearchService.getArticleTitle(comment.getCommentArticleId()),
                    comment.getCommentId(),
                    commentUserId,
                    comment.getCommentArticleId(),
                    comment.getCommentFarther(),
                    comment.getCommentContent(),
                    new Date(comment.getCreateTime().getTime()),
                    new Date(comment.getUpdateTime().getTime())
            );
            commentAndArticleNameList.add(commentAndArticleName);
        }
        return ServerResponse.createBySuccess("你发表的评论",commentAndArticleNameList);
    }

    //删除评论，删除文章用
    public void deleteCommentsForDeleteArticle(String articleId){
        List<Comment> commentList=commentMapper.selectCommentByArticleIdAndFartherId(articleId,"0");
        for (Comment comment : commentList){
            //删除redis中的评论和mysql中的
            deleteComments(comment.getCommentId());
        }
    }

    //删除评论及其子评论
    private void deleteComments(String commentId){
        List<Comment> commentList=commentMapper.selectCommentByFartherId(commentId);
        if (commentList.size()==0){
            JedisUtil.delKey(Const.RedisKey.BeforeCommentKeyId+commentId);
            commentMapper.deleteByPrimaryKey(commentId);
        }else {
            for (Comment comment : commentList){
                deleteComments(comment.getCommentId());
            }
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
                    userMapper.selectByPrimaryKey(comment.getCommentUserId()).getUserName(),
                    comment.getCommentUserId(),
                    comment.getCommentArticleId(),
                    comment.getCommentFarther(),
                    comment.getCommentContent(),
                    false,
                    new Date(comment.getCreateTime().getTime()),
                    new Date(comment.getUpdateTime().getTime())
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
                        userMapper.selectByPrimaryKey(comment.getCommentUserId()).getUserName(),
                        comment.getCommentUserId(),
                        comment.getCommentArticleId(),
                        comment.getCommentFarther(),
                        comment.getCommentContent(),
                        false,
                        new Date(comment.getCreateTime().getTime()),
                        new Date(comment.getUpdateTime().getTime())
                );
                commentVoList.add(commentVo);
            }
            return commentVoList;
        }
        return null;
    }

}
