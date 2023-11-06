package com.jina.proj.usr.login.web;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @RequestMapping(value = { "/" , "/login" })
    public String main(HttpServletRequest request){
        // 권한 확인 후 있으면 main 페이지로 이동
        return "usr/login";
    }
    
    @RequestMapping()
    public void login(@ResponseBody Account account, HttpServletRequest request, HttpServletResponse response){
        loginServiceImpl.login(account, request, response);
    }

    /**
     * @methodName join
     * @description 회원가입
     * @return ResponseEntity
     */
    @RequestMapping(value = "/user/join")
    @ResponseBody
    public ResponseEntity join(Account account) throws Exception {
        try{
            loginServiceImpl.join(account);
            return new ResponseEntity<>("회원가입에 성공했습니다.", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @methodName findByUserId
     * @description id 중복체크
     * @return ResponseEntity
     */
    @RequestMapping(value = "/user/findByUserId")
    @ResponseBody
    public ResponseEntity findByUserId( @RequestParam(value = "userId") String userId){

        try{
            Optional<Account> usrInfo  = accountRepository.findByUserId(userId);
            int chk = usrInfo.isPresent() ? 1 : 0;
            return ResponseEntity.status(HttpStatus.OK).body(chk);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
