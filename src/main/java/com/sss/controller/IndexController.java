package com.sss.controller;

import com.sss.model.User;
import com.sss.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Random;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;


    @RequestMapping("/getUser")
    @ResponseBody
    public List<User> home(){
        return userService.getUsers();
    }


    @RequestMapping("/insert")
    @ResponseBody
    public List<User> insert(){

        Random random = new Random();
        User user  = new User();
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        user.setName(String.format("USER%d", 1));
        user.setPassword("1");
        user.setSalt("1");
        user.setId(1);
        userService.insert(user);

        return userService.getUsers();
    }
}
