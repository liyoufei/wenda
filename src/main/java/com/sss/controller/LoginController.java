package com.sss.controller;


import com.sss.Util.DataUtil;
import com.sss.async.EventModel;
import com.sss.async.EventProducer;
import com.sss.async.EventType;
import com.sss.model.EntityType;
import com.sss.model.User;
import com.sss.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    EventProducer eventProducer;

//    跳转到注册登录页面
    @RequestMapping(path = "/reglogin",method = RequestMethod.GET)
    public String reglogin(@RequestParam(value = "next",required = false) String next,Model model){
        model.addAttribute("next",next);
        return "login";
    }


    @RequestMapping(path = "/register/",method = RequestMethod.POST)
    public String register(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "rememberme",defaultValue = "false") Boolean rememberme,
                        @RequestParam(value = "next",required = false) String next,
                        HttpServletResponse httpServletResponse,
                        Model model){
        try{
            Map<String,Object> map = userService.register(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
//                用于将cookie设置为服务器全路径共享
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*7);
                }
                httpServletResponse.addCookie(cookie);
                if(!StringUtils.isEmpty(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else{
//                将登录时产生的错误传递到前端
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常",e.getMessage());
//            注册失败重新返回登录页面
            return "login";
        }

    }
//    用于对登录操作的判断
    @RequestMapping(path = "/login/",method = RequestMethod.POST)
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "rememberme",defaultValue = "false") Boolean rememberme,
                        @RequestParam(value = "next",required = false) String next,
                        HttpServletResponse httpServletResponse,
                        Model model){
        try{
            Map<String,Object> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
//                用于将cookie设置为服务器全路径共享
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*7);
                }
                httpServletResponse.addCookie(cookie);
                eventProducer.fireEvent(new EventModel(EventType.LOGIN));
                if(!StringUtils.isEmpty(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else{
//                将登录时产生的错误传递到前端
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("登录异常",e.getMessage());
//            登录失败重新返回登录页面
            return "login";
        }

    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public void logout(@CookieValue(value = "ticket") String ticket,HttpServletResponse httpServletResponse){
        userService.logout(ticket,1);
        try{
            httpServletResponse.sendRedirect("/");
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
