package com.sss.dao;

import com.sss.model.LoginTicket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoginTicketMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoginTicket record);

    LoginTicket selectByPrimaryKey(Integer id);

    List<LoginTicket> selectAll();

    int updateByPrimaryKey(LoginTicket record);

    LoginTicket selectByTicket(String ticket);

    int changeStatus(@Param(value = "ticket") String ticket, @Param(value = "status") int status);
}