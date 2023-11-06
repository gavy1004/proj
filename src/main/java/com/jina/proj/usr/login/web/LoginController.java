package com.jina.proj.usr.login.web;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jina.proj.usr.login.repository.AccountRepository;
import com.jina.proj.usr.login.service.Impl.LoginServiceImpl;
import com.jina.proj.vo.Account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final LoginServiceImpl loginServiceImpl;
    private final AccountRepository accountRepository;
    
    /** 
     * @description 메인 화면 
     * @return main or login 페이지  
     */
    @RequestMapping( value = { "/" , "/login" })
    public String main(HttpServletRequest request){
        // 권한 확인 후 있으면 main 페이지로 이동
        return "usr/login";
    }

    @RequestMapping(value = "/user/loginAction.do")
    public String loginAction(){
        
        return "";  
    }

    @RequestMapping(value = "/user/join")
    @ResponseBody
    public String join(Account account){
        System.out.println("히히");
        /*Optional<Account> usrInfo = accountRepository.findById(account.getId());
        System.out.println(usrInfo);*/

        //loginServiceImpl.
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
