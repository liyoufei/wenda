package com.sss.interceptor;

import com.sss.dao.LoginTicketMapper;
import com.sss.dao.UserMapper;
import com.sss.model.HostHolder;
import com.sss.model.LoginTicket;
import com.sss.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;


    //在controller方法调用之前操作，后续的方法只有在本方法返回值为true时才会继续操作，先注册地的interceptor
    //的prehandler先执行，先注册的posthandler后执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
//        为了取出本次请求中cookie的ticket字段
        if(request.getCookies() != null){
            Cookie[] cookies = request.getCookies();
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
//        根据ticket来获取本次请求的已登录用户
        if(ticket != null){
            LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                return true;
            }
//          将用户放入hostholder中以便
            User user = userMapper.selectByPrimaryKey(loginTicket.getUserId());
            hostHolder.setUsers(user);
            return true;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        在视图渲染之前将用户对象装入ModelAndView中
        if(hostHolder.getUser() != null && modelAndView != null){
            modelAndView.addObject("user",hostHolder.getUser());

        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

//        这次请求完成，视图渲染完成后保证能够清楚必要的数据
        hostHolder.clear();
    }
}
