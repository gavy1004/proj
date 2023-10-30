package com.jina.proj.usr;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.DefaultResourceLoader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
    
    /** 
     * @description 메인 화면 
     * @return main or login 페이지  
     */
    @RequestMapping(value={"/","/login.do"})
    public String main(HttpServletRequest request){
        log.info("이제된다고 말해 ");
        return "/view/login";
    }

    @RequestMapping(value = "/loginAction.do")
    public String loginAction(){
        log.info("이제 되려나  ㅎㅎㅎㅎ ");
        return "";  
    }
}
