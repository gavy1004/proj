package com.jina.proj.config.security;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.jina.proj.usr.login.repository.TokenRepository;
import com.jina.proj.vo.Account;
import com.jina.proj.vo.TokenInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @description Jwt 토큰 생성 
 */

@Slf4j
@Component
public class JwtProvider {
    private final TokenRepository tokenRepository;

    private Key secretKey;

    @Value("${jwt.key}")
    private String salt;

    @Value("${jwt.accExpt}")
    private int ACCESS_TOKEN_EXPIRE_TIME; // 엑세스 토큰 유효 기간

    @Value("${jwt.refExpt}")
    private int REFRESH_TOKEN_EXPIRE_TIME; // 리프레쉬 토큰 유효 기간

    public JwtProvider(@Value("${jwt.key}") String jwtKey, TokenRepository tokenRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtKey); // BASE64 디코딩
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.tokenRepository = tokenRepository;
    }
    /**
     * @description 토큰 재발급
     * @param authentication 권한
     * @return newToken 토큰 정보
     */
    public TokenInfo refresh(Authentication authentication) {
        // 권한 정보의 사용자 계정을 통해 DB에서 리프래쉬 토큰 정보를 가져온다.
        TokenInfo oldToken = tokenRepository.findByUsrId(authentication.getName())
                .orElseThrow(() -> new JwtException("사용자 리프래쉬 토큰을 찾을 수 없습니다."));

        // 리프래쉬 토큰의 유효성 검사
        if(!validateToken(oldToken.getRefsToken())) {
            throw new JwtException("리프래쉬 토큰이 유효하지 않습니다.");
        }

        TokenInfo newToken = createToken(authentication); // 새로운 엑세스/리프래쉬 토큰 생성
        tokenRepository.save(newToken); // 토큰 정보 저장

        return newToken;
    }


    /**
     * @description 토큰 클레임 파싱
     * @param accToken
     * @return Claims
     */
    public Claims parseClaims(String accToken) {
        try{
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accToken).getBody();
        }catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return e.getClaims();
        }
    }

    /**
     * @description 사용자 ID 파싱
     * @param usrId
     * @return usrId
     */
    public String parseUsrId(String usrId) {
        return usrId.contains("_") ? usrId.split("_")[0] : usrId; // 사용자 ID만 가져온다.
    }

    /**
     * @description 토큰 생성 
     */
    public TokenInfo createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        Account account = (Account) authentication.getPrincipal(); // 사용자 정보
        String usrId = parseUsrId(authentication.getName());
        long now = (new Date()).getTime();

        // 엑세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(usrId + "_" + Long.toString(now)) // 사용자 ID + 현재 시간(중복 로그인 가능)
                .claim("auth", authorities) // 사용자 권한
                .claim("authCd", account.getAuthCd())
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME)) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS512) // 서명
                .compact();


        // 리프래쉬 토큰 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        // 토큰 정보 생성
        return TokenInfo.builder()
                .usrId(usrId + "_" + Long.toString(now))
                .accToken(accessToken)
                .refsToken(refreshToken)
                .build();
    }
    
    /**
     * @description 쿠키에 엑세스 토큰 저장
     * @param tokenInfo 토큰 정보
     * @param response
     */
    public void setCookie(TokenInfo tokenInfo, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", tokenInfo.getAccToken()); // 토큰 쿠키 생성

        cookie.setPath("/");
        cookie.setHttpOnly(true); // 클라이언트가 쿠키 접근 방지 (document.cookie)
        cookie.setMaxAge(ACCESS_TOKEN_EXPIRE_TIME / 1000); // 쿠키 만료 시간 설정(초)
        response.addCookie(cookie); // 쿠키 추가
    }

    public String getCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }catch (NullPointerException e) {
            log.info("쿠키가 존재하지 않습니다.");
            return null;
        }
    }

    public Authentication getAuthentication(String accToken) {
        Claims claims = parseClaims(accToken);

        // 클레임에서 권한 정보 가져온다.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        log.info("authorities = "+authorities);
        // UserDetailsImpl 객체 생성
        Account principal = Account.builder()
                .userId(claims.getSubject())
                .userPwd("").role(authorities)
                .build();

        // 권한이 있는 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(principal,"", authorities);
    }


    // 토큰에 담겨있는 유저 account 획득
    public String getAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰 검증 
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            log.info(token);
            return true;
        }catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        }catch(ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        }catch(UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT토큰입니다.");
        }catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
