package com.jina.proj.usr.login.service.Impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jina.proj.usr.login.service.NaverService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service(value = "naverService")
public class NaverServiceImpl implements NaverService{

    @Value("${oauth.naver.clientId}")
    private String CLIENT_ID;

    @Value("${oauth.naver.redirectURL}")
    private String REDIRECT_URL;

    @Override
    public String createNaverURL() throws UnsupportedEncodingException {
        StringBuffer url = new StringBuffer();

        // 카카오 API 명세에 맞춰서 작성
        String redirectURI = URLEncoder.encode(REDIRECT_URL, "UTF-8"); // redirectURI 설정 부분
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();

        url.append("https://nid.naver.com/oauth2.0/authorize?response_type=code");
        url.append("&client_id=" + CLIENT_ID);
        url.append("&state=" + state);
        url.append("&redirect_uri=" + redirectURI);
        log.info("redirectURI === "+REDIRECT_URL);
        
        return url.toString();
    }
    
}
