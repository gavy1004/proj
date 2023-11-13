package com.jina.proj.usr.login.web;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jina.proj.config.security.JwtProvider;
import com.jina.proj.usr.login.repository.AccountRepository;
import com.jina.proj.usr.login.service.Impl.LoginServiceImpl;
import com.jina.proj.vo.Account;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final LoginServiceImpl loginServiceImpl;
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;
    
    /** 
     * @description 메인 화면 
     * @return main or login 페이지 
     */
    @RequestMapping(value = { "/" , "/login" })
    public String main(HttpServletRequest request){
        String accJwt = jwtProvider.getCookie(request);

        if(StringUtils.hasText(accJwt) && jwtProvider.validateToken(accJwt)){
            return "redirect:/main";

        }else{
            return "usr/login";
        }
    }
    
    @RequestMapping(value = { "/main"  })
    public String dashboard(HttpServletRequest request){
        return "dashboard";
    }

    @RequestMapping(value = { "/logout"})
    public void logout (HttpServletRequest request, HttpServletResponse response) throws IOException {
        loginServiceImpl.logout(request, response);
    }
    
    @RequestMapping(value = "/user/actionLogin")
    public void login(Account account, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
