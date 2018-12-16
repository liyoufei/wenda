package com.sss.service;


import com.sss.dao.MessageMapper;
import com.sss.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;


    public int sendMessage(Message message){
        return messageMapper.insert(message);
    }

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageMapper.getConversationList(userId,offset,limit);
    }

    public int getUnreadCount(int userId,String conversationId){
        return messageMapper.getUnreadCount(userId,conversationId);
    }

    public List<Message> getConversationDetail(String conversationId,int offset ,int limit){
        return messageMapper.getConversationDetail(conversationId, offset, limit);
    }

}
