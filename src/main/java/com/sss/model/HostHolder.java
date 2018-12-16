package com.sss.model;


import org.springframework.stereotype.Component;

@Component
public class HostHolder {

//    threadlocal为每个线程保存一个变量副本，都能读而不存在线程不安全的问题
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
