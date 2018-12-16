package com.sss.dao;

import com.sss.model.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    Comment selectByPrimaryKey(Integer id);

    List<Comment> selectAll();

    int updateByPrimaryKey(Comment record);

    int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    List<Comment> getCommentByEntity(@Param("entityId") int entityId,@Param("entityType") int entityType);

    int updateStatus(@Param("entityId") int entityId,@Param("entityType") int entityType,@Param("status") int status);
}