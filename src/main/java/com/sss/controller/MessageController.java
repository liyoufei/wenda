package com.sss.controller;


import com.sss.model.*;
import com.sss.service.MessageService;
import com.sss.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger  = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/msg/list",method = {RequestMethod.GET})
    public String getMessageList(Model model){
        try{
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversations = messageService.getConversationList(localUserId,0,10);
            ArrayList<ViewObject> viewObjects = new ArrayList<>();
            for (Message message : conversations
                 ) {
                ViewObject viewObject = new ViewObject();
                viewObject.set("message",message);
                //列表用于展示的对话用户，不是已登录的用户自己
                int targetUserId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                User user = userService.getUser(targetUserId);
                viewObject.set("user",user);
                viewObject.set("unread",messageService.getUnreadCount(localUserId,message.getConversationId()));
                viewObjects.add(viewObject);
            }
            model.addAttribute("vos",viewObjects);


        }catch (Exception e){
            logger.error("获取消息列表失败"+e.getMessage());
        }

        return "letter";
    }

    @RequestMapping(value = "/msg/detail",method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @Param(value = "conversationId") String conversationId){
        try{
            List<Message> messages = messageService.getConversationDetail(conversationId,0,10);
            ArrayList<ViewObject> viewObjects = new ArrayList<>();
            for (Message message : messages
                 ) {
                ViewObject viewObject = new ViewObject();
                viewObject.set("message",message);
                User user = userService.getUser(message.getFromId());
                if(user == null){
                    continue;
                }
                viewObject.set("userId",user.getId());
                viewObject.set("headUrl",user.getHeadUrl());

                viewObjects.add(viewObject);
            }

            model.addAttribute("vos",viewObjects);
        }catch (Exception e){
            logger.error("获取消息详情失败"+e.getMessage());
        }

        return "letterDetail";
    }

    @RequestMapping(value = "/msg/addMessage",method = {RequestMethod.POST})
    @ResponseBody
    public Infomation sendMessage(@RequestParam("toName") String toName,@RequestParam("content") String content){

        Infomation info = new Infomation();
        try{


            Message message = new Message();
            message.setContent(content);
            if(hostHolder.getUser() != null){
                message.setFromId(hostHolder.getUser().getId());
            }else {
                info.setCode(1);
                info.setMsg("用户未登录");
                return info;
            }

            if(userService.selectUserByName(toName) == null){
                info.setMsg("用户名不存在");
                info.setCode(2);
                return info;
            }
            message.setCreatedDate(new Date());
            message.setHasRead(0);
            message.setToId(userService.selectUserByName(toName).getId());
            message.setConversationId(message.getFromId() < message.getToId() ? String.format("%d_%d", message.getFromId(), message.getToId()) : String.format("%d_%d", message.getToId(), message.getFromId()));
            messageService.sendMessage(message);
            info.setCode(0);
            return info;

        }catch (Exception e){
            logger.error("发送信息失败"+e.getMessage());
            info.setCode(3);
            return info;
        }
    }

}
