package com.jina.proj.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @description JWT 권한별 메뉴 접근 허가 클래스
 * @packageName com.egiskorea.jwt
 * @class JwtAuthChecker.java
 * @version 1.0
 * @see
 */

@Slf4j
@RequiredArgsConstructor
@Component("JwtAuthChecker")
public class JwtAuthChecker {

    private final JwtProvider jwtProvider;

    /**
     * @description 접근 권한 체크
     * @param request
     * @return boolean
     * @throws IOException
     */
    public boolean checkAuthURI(HttpServletRequest request) throws IOException {
        String accJwt = jwtProvider.getCookie(request); // 엑세스 토큰

        // 엑세스 토큰 null, 유효성 체크
        if(accJwt == null || !jwtProvider.validateToken(accJwt)) {
            return false;
        }

        Authentication authentication = jwtProvider.getAuthentication(accJwt); // 토큰 인증 정보
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 권한 목록
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        GrantedAuthority authority = authorities.iterator().next(); // 사용자마다 부여되는 권한 ID가 하나인 경우에만 iterator 사용
        String authInfoId = authority.getAuthority(); // 권한 정보 ID
        log.info("authInfoId = "+authInfoId);
        log.info("authority = "+authority);
        log.info("요청 URL 패턴 : {}", request.getRequestURI());

        return true;
    }
}
