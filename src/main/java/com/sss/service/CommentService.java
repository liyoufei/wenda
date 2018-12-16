package com.sss.service;

import com.sss.dao.CommentMapper;
import com.sss.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;

    public List<Comment> getCommentList(int entityId,int entityType){
        return commentMapper.getCommentByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        return commentMapper.insert(comment);
    }

    public int deleteComment(int entityId,int entityType,int status){
        return commentMapper.updateStatus(entityId,entityType,status);
    }

    public int getCommentCount(int entityId,int entityType){
        return commentMapper.getCommentCount(entityId,entityType);
    }

    public Comment getComment(int id){
        return commentMapper.selectByPrimaryKey(id);
    }
}
