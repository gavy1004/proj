package com.jina.proj.usr;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
    
    /** 
     * @description 메인 화면 
     * @return main or login 페이지  
     */
    /*@RequestMapping(value={"/","/login.do"})
    public String main(HttpServletRequest request){
        return "/view/login";
    }*/

    @RequestMapping(value = "/loginAction.do")
    public String loginAction(){
        log.info("이제 되려나  ㅎㅎㅎㅎ ");
        return "";  
    }
}
