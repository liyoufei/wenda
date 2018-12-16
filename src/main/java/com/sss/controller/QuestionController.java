package com.sss.controller;


import com.sss.Util.DataUtil;
import com.sss.dao.CommentMapper;
import com.sss.dao.QuestionMapper;
import com.sss.dao.UserMapper;
import com.sss.model.*;
import com.sss.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {

    private Logger logger = LoggerFactory.getLogger(Question.class);
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeService likeService;



    @RequestMapping(value = "/question/{qid}",method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionMapper.selectByPrimaryKey(qid);
        model.addAttribute("question",question);
        List<Comment> comments = commentMapper.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> viewObjects = new ArrayList<>();
        for (Comment comment : comments
             ) {
            ViewObject viewObject = new ViewObject();
            viewObject.set("comment",comment);
            if(hostHolder.getUser() == null){
                viewObject.set("liked",0);
            }else {
                viewObject.set("liked",likeService.getLikeStatus(comment.getId(),EntityType.ENTITY_COMMENT,hostHolder.getUser().getId()));
            }
            viewObject.set("likeCount",likeService.getLikeCount(comment.getId(),EntityType.ENTITY_COMMENT));
            viewObject.set("user",userMapper.selectByPrimaryKey(comment.getUserId()));
            viewObjects.add(viewObject);
        }
        model.addAttribute("comments",viewObjects);

        return "detail";

    }

    @RequestMapping(value = "/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public Question addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){

        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        if(hostHolder.getUser() == null){
            question.setUserId(DataUtil.ANONYMOUS_USER);
        }else{
            question.setUserId(hostHolder.getUser().getId());
        }
        question.setCreatedDate(new Date());
        question.setCommentCount(0);

        try {
            questionMapper.insert(question);
        }catch (Exception e){
            logger.error("增加问题失败："+e.getMessage());
        }

        return question;

    }
}
