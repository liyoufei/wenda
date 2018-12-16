package com.sss.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sss.Util.JedisAdapter;
import com.sss.Util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
            for(Map.Entry<String,EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventType();

                for (EventType eventType : eventTypes){
                    if(!config.containsKey(eventType)){
                        config.put(eventType,new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }

        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getBizEventqueue();
                    List<String> events = jedisAdapter.brpop(0,key);

                    for(String message : events){
                        if(message.equals(key)){
                            continue;
                        }

                        ObjectMapper objectMapper = new ObjectMapper();

                        try{
                            EventModel eventModel = objectMapper.readValue(message,EventModel.class);
                            if(!config.containsKey(eventModel.getType())){
                                logger.error("不能识别的事件");
                                continue;
                            }
                            for(EventHandler eventHandler : config.get(eventModel.getType())){
                                eventHandler.doHandle(eventModel);
                            }
                        }catch (Exception e){
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        });

        thread.start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}