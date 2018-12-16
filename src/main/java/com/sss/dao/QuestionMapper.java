package com.sss.dao;

import com.sss.model.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Question record);

    Question selectByPrimaryKey(Integer id);

    List<Question> selectAll();

    int updateByPrimaryKey(Question record);

//    多参数传递，参数前要加注解
    List<Question> selectLatestQuestion(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    int updateCommentCount(@Param("qid") int qid,@Param("count") int count);
}