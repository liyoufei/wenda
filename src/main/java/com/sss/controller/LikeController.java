package com.sss.controller;

import com.sss.async.EventModel;
import com.sss.async.EventProducer;
import com.sss.async.EventType;
import com.sss.model.Comment;
import com.sss.model.EntityType;
import com.sss.model.HostHolder;
import com.sss.model.Infomation;
import com.sss.service.CommentService;
import com.sss.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;
    @RequestMapping(value = "/like",method = {RequestMethod.POST})
    @ResponseBody
    public Infomation like(@RequestParam("commentId") int commentId){
        Infomation info = new Infomation();
        if(hostHolder.getUser() == null){
            info.setCode(999);
            info.setMsg("用户未登录");
            return info;
        }

        Comment comment = commentService.getComment(commentId);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_COMMENT)
                                                .setEntityId(commentId).setEntityOwnerId(comment.getUserId())
                                                .setExt("questionId",String.valueOf(comment.getEntityId())));
        long likeCount = likeService.like(commentId, EntityType.ENTITY_COMMENT,hostHolder.getUser().getId());
        info.setCode(0);
        info.setMsg(String.valueOf(likeCount));
        return info;

    }

    @RequestMapping(value = "/dislike",method = {RequestMethod.POST})
    @ResponseBody
    public Infomation dislike(@RequestParam("commentId") int commentId){
        Infomation info = new Infomation();
        if(hostHolder.getUser() == null){
            info.setCode(999);
            info.setMsg("用户未登录");
            return info;
        }

        long likeCount = likeService.dislike(commentId,EntityType.ENTITY_COMMENT,hostHolder.getUser().getId());
        info.setCode(0);
        info.setMsg(String.valueOf(likeCount));
        return info;
    }

}
