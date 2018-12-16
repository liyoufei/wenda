package com.sss.service;

import com.sss.Util.JedisAdapter;
import com.sss.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    public long getLikeCount(int entityId,int entityType){
        String key = RedisKeyUtil.getLikeKey(entityId, entityType);
        return jedisAdapter.scard(key);
    }

    public long like(int entityId,int entityType,int userId){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);

        if(jedisAdapter.sismember(dislikeKey,String.valueOf(userId))){
            jedisAdapter.srem(dislikeKey,String.valueOf(userId));
        }

        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);

    }

    public long dislike(int entityId,int entityType,int userId){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);

        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            jedisAdapter.srem(likeKey,String.valueOf(userId));
        }

        jedisAdapter.sadd(dislikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public int getLikeStatus(int entityId,int entityType,int userId){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);

        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }else if(jedisAdapter.sismember(dislikeKey,String.valueOf(userId))){
            return -1;
        }else{
            return 0;
        }

    }
}
