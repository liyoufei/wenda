package com.sss.controller;



import com.sss.Util.DataUtil;
import com.sss.model.Comment;
import com.sss.model.EntityType;
import com.sss.model.HostHolder;

import com.sss.service.CommentService;
import com.sss.service.QuestionService;
import com.sss.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @RequestMapping(value = "/addComment",method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int qid,@RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if(hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(DataUtil.ANONYMOUS_USER);
            }

            comment.setCreatedDate(new Date());
            comment.setEntityId(qid);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setStatus(0);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(qid,EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(qid,count);


        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/question/" + String.valueOf(qid);

    }



}
