package com.jina.proj.usr.login.web;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jina.proj.usr.login.service.CustomOAuth2UserService;
import com.jina.proj.usr.login.service.NaverService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OauthController {

    private final NaverService naverService;
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * 프론트에 Redirect URI를 제공하기 위한 메소드
     * 프론트에서 네이버 인증 센터로 요청을 주기위한 URI를 제공하며, 이를통해 인증코드를 받아 자체 서비스 callback API 호출시 전달
     *
     * @return redirect URI
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value={ "/oauth/naver" })
    public ResponseEntity<?> naverConnect() throws UnsupportedEncodingException {
        String url = naverService.createNaverURL(); //  네이버 지정 URL + 본인앱의_clientId + 암호화용_state값 + 인코딩된_Redirect_URL 이 반환되어야 한다.
        return new ResponseEntity<>(url, HttpStatus.OK); // 프론트 브라우저로 보내는 주소
    }

    @RequestMapping(value= {"/oauth/authorization/kakao"})
    public ResponseEntity<?> kakaoConnect() throws UnsupportedEncodingException {
        String url = customOAuth2UserService.createKakaoURL(); //  네이버 지정 URL + 본인앱의_clientId + 암호화용_state값 + 인코딩된_Redirect_URL 이 반환되어야 한다.
        log.info(url);
        return new ResponseEntity<>(url, HttpStatus.OK); // 프론트 브라우저로 보내는 주소
    }

    @GetMapping(value= {"/oauth/kakao"})
    public String  kakaoCallback(@RequestParam("code") String code) throws Exception {
        log.info("kakaoCallback () ");
        // URL에 포함된 code를 이용하여 액세스 토큰 발급
        String accessToken = customOAuth2UserService.getKakaoAccessToken(code);
        log.info("accessToken ="+accessToken);


        // 액세스 토큰을 이용하여 카카오 서버에서 유저 정보(닉네임, 이메일) 받아오기
        
        HashMap<String, Object> userInfo = customOAuth2UserService.getKakaoUserInfo(accessToken);

        log.info(userInfo.toString());
        
        return "usr/login";
    }


}
