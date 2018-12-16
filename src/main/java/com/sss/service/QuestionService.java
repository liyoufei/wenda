package com.sss.service;

import com.sss.dao.QuestionMapper;
import com.sss.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    public List<Question> selectLatestQuestion(int userId,int offset,int limit){
        return questionMapper.selectLatestQuestion(userId,offset,limit);
    }

    public int updateCommentCount(int question,int count){
        return questionMapper.updateCommentCount(question,count);
    }
}
