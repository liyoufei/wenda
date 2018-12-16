package com.sss.service;


import com.sss.Util.DataUtil;
import com.sss.dao.LoginTicketMapper;
import com.sss.dao.UserMapper;
import com.sss.model.LoginTicket;
import com.sss.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public List<User> getUsers(){
        return userMapper.selectAll();
    }

    public User selectUserByName(String name){
        return userMapper.selectByName(name);
    }

    public User getUser(int id){
        return userMapper.selectByPrimaryKey(id);
    }

    public void insert(User user){
        userMapper.insert(user);
    }

    public Map<String,Object> register(String username,String password){
        Map<String,Object> map = new HashMap<>();
//        判断注册输入是否有效
        if(StringUtils.isEmpty(username)){
            map.put("msg","登录名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg","密码不能为空");
            return map;
        }

//        根据用户名获得用户实体
        User user = userMapper.selectByName(username);
        if(user != null) {
            map.put("msg", "用户已存在");
            return map;
        }
        User newUser = new User();
        newUser.setName(username);
        String salt = UUID.randomUUID().toString().substring(0,5);
        newUser.setSalt(salt);
        newUser.setPassword(DataUtil.MD5(password+salt));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        newUser.setHeadUrl(head);
        userMapper.insert(newUser);
        int ID = userMapper.selectByName(username).getId();
//        注册成功后生成ticket存入数据库
        String ticket = addLoginTicket(ID);
        map.put("ticket",ticket);
        return map;

    }

    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<>();
//        判断登录输入是否有效
        if(StringUtils.isEmpty(username)){
            map.put("msg","登录名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg","密码不能为空");
            return map;
        }
//        根据用户名获得用户实体
        User user = userMapper.selectByName(username);
        if(user == null) {
            map.put("msg", "用户不存在");
            return map;
        }else if(!DataUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }else{
//        如果通过密码检查则登陆成功，将生成ticket存入数据库
            String ticket = addLoginTicket(user.getId());
            map.put("ticket",ticket);
            return map;
        }

    }

    public void logout(String ticket,int status){
        loginTicketMapper.changeStatus(ticket,status);
    }

    private String addLoginTicket(int userId){

//      登录时存入数据库的ticket作为判断用户是否登陆的判断
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);

        Date date = new Date();
        date.setTime(date.getTime() + 3600*24*7);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        String ticket = UUID.randomUUID().toString().replaceAll("-","");
        loginTicket.setTicket(ticket);
//      存入数据库并返回本次的ticket
        loginTicketMapper.insert(loginTicket);
        return loginTicket.getTicket();
    }



}
