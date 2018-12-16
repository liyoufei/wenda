package com.sss.async.handler;

import com.sss.Util.DataUtil;
import com.sss.async.EventHandler;
import com.sss.async.EventModel;
import com.sss.async.EventType;
import com.sss.model.Message;
import com.sss.model.User;
import com.sss.service.MessageService;
import com.sss.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {

        Message message = new Message();
        message.setFromId(DataUtil.SYSTEM_USER);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());

        message.setContent("用户"+user.getName()+"赞了你的评论，http://127.0.0.1:8080/question/"+eventModel.getExt("questionId"));
        message.setConversationId(String.format("%d_%d",DataUtil.SYSTEM_USER,eventModel.getEntityOwnerId()));
        messageService.sendMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LIKE);
    }
}
