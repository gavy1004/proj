package com.jina.proj.usr.login.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jina.proj.vo.Account;

public interface LoginService {

    public void join(Account account) throws Exception;

    public void login(Account account, HttpServletRequest request, HttpServletResponse response) throws Exception;
} 