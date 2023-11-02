package com.jina.proj.usr;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    /*@RequestMapping(value = "/userList.do")
    @ResponseBody
    public ResponseEntity<List<UsrInfo>> dlf(){
        // 도시계획 조회
        List<UsrInfo> resultList = cpService.selectCityPlan(cityPlan);

        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }*/

}
