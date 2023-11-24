package com.jina.proj.usr.login.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {
    
    @Value("${oauth.naver.clientId}")
    private String CLIENT_ID;

    @Value("${oauth.naver.redirectURL}")
    private String REDIRECT_URL;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2User **************************************");
        OAuth2User oAuth2User = super.loadUser(userRequest);
 
        // Role generate
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");
 
        // nameAttributeKey
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        
        // DB 저장로직이 필요하면 추가
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }

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

    public String createKakaoURL() throws UnsupportedEncodingException {
        log.info("createKakaoURL () ");
        StringBuffer url = new StringBuffer();

        // 카카오 API 명세에 맞춰서 작성
        String redirectURI = URLEncoder.encode("http://localhost:8160/oauth/kakao", "UTF-8"); // redirectURI 설정 부분
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();
        
        url.append("https://kauth.kakao.com/oauth/authorize?response_type=code");
        url.append("&client_id=" + "6dc30ede4b65bf92996819f7b936c627");
        url.append("&state=" + state);
        url.append("&redirect_uri=" + redirectURI);
        
        return url.toString();
    }
    public HashMap<String, Object> getKakaoUserInfo(String token) throws Exception {
        log.info("getKakaoUserInfo () ");
        HashMap<String, Object> userInfo = new HashMap<>();
        String postURL = "https://kapi.kakao.com/v2/user/me";

        URL url = new URL(postURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int responseCode = conn.getResponseCode();

        if(responseCode == 200){
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            String jsonString = result.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            log.info(jsonString);

            String nickname = jsonNode.path("kakao_account").path("profile").path("nickname").asText();
            String email = jsonNode.path("kakao_account").path("email").asText();
            
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);
        }
   

        return userInfo;
    }


    public String getKakaoAccessToken(String code) throws Exception {
        log.info("getKakaoAccessToken () ");
        String accessToken = "";
        String refreshToken = "";
        
        String reqURL = "https://kauth.kakao.com/oauth/token";

        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // POST 요청을 위해 기본값이 false인 setDoOutput을 true로 설정
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // POST 요청에 필요로 요구하는 파라미터를 스트림을 통해 전송
        BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(conn.getOutputStream())));
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id="+"6dc30ede4b65bf92996819f7b936c627");
        sb.append("&redirect_uri=http://localhost:8160/oauth/kakao");
        sb.append("&code=" + code);
        bw.write(sb.toString());
        bw.flush();

        int responseCode = conn.getResponseCode();
        log.info("responseCode : " + responseCode);

        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        String jsonString = result.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        accessToken = jsonNode.get("access_token").asText();
        refreshToken = jsonNode.get("refresh_token").asText();
        log.info("accessToken : " + accessToken);
        log.info("refreshToken : " + refreshToken);

        bufferedReader.close();
        bw.close();

        bw.close();
        return accessToken;
    }

}
