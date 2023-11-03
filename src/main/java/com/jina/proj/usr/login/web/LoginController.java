package com.jina.proj.usr.login.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
    
    
    /** 
     * @description 메인 화면 
     * @return main or login 페이지  
     */
    @RequestMapping( value = { "/" , "/login" })
    public String main(HttpServletRequest request){
        // 권한 확인 후 있으면 main 페이지로 이동
        return "usr/login";
    }

    @RequestMapping(value = "/loginAction.do")
    public String loginAction(){
        
        return "";  
    }

    @RequestMapping(value = "/user/join.do")
    @ResponseBody
    public String join(@Param("param") String msg){
        System.out.println("히히"+msg);
        return "";  
    }

    /*  @RequestMapping(value = "/userList.do")
    @ResponseBody
    public ResponseEntity<List<Account>> dlf(){
        // 도시계획 조회
       // List<Account> resultList = cpService.selectCityPlan(cityPlan);

        return ResponseEntity.status(HttpStatus.OK).body();
    }*/

}
