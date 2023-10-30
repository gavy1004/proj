package com.jina.proj.config.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 웹 시큐리티 설정 클래스
 * @packageName com.jina.proj.config.security
 * @class SecurityConfig.java
 * @since 2023-10-25
 * @version 1.0
 * @see
 *
 * << 개정이력(Modification Information) >>
 * 수정일        수정자          수정내용
 * ----------   --------   ---------------------------
 * 2023-10-25	지나         최초 생성
 *
 */

@Slf4j
@Configuration
public class SecurityConfig {
    // 모든 요청은 인증이 되어야 자원에 접근 가능 

    /**
     * @description BCrypt 알고리즘 비밀번호 암호화  
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @description 웹 보안 구성  
     */
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().mvcMatchers( // 특정 요청 패턴 무시 
               // "/view/**",
                "/js/**",
                "/css/**",
                "/plugins/**",
                "/login",
                "/logout",
                "/favicon.ico",
                //"/",
                "/error",
                "/join"
        );
    }

    /**
     * @description HttpSecurity 구성  
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(requests -> requests
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/view/login")
            .loginProcessingUrl("/loginProc")
            .usernameParameter("id")
            .passwordParameter("pw")
            .defaultSuccessUrl("/view/dashboard", true)
            .permitAll()
        ).logout(logout -> {});

        // 해당기능 사용하기 위해서는 프론트 단에서 csrf토큰 값 보내줘야함 
        //.httpBasic(basic ->basic.disable())
        //.formLogin(login ->login.disable())
        //.cors().disable()   
        //.csrf().disable()
        return http.build();
    }
    
    /**
     * @description 접근 거부(403) 예외 처리
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            if(isAxios(request)) { // 클라이언트
                response.setStatus(response.SC_UNAUTHORIZED); // 401
            } else { // 서버
                String referer = request.getHeader("Referer"); // 최근 접속 URL
                response.sendRedirect(referer); // 이전 페이지로 리디렉션
            }
            log.error("URI : {}, 접근 권한이 없습니다.", request.getRequestURI());
        };
    }

    /**
     * @description 인증되지 않은 사용자(403) 예외 처리
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            SecurityContextHolder.clearContext();
            if(isAxios(request)) { // 클라이언트
                response.setStatus(response.SC_FORBIDDEN); // 403
            } else { // 서버
                response.sendRedirect("/login");
            }
            log.error("URI : {},  인증되지 않은 사용자입니다.", request.getRequestURI());
        };
    }

    /**
     * @description cors 설정
     * @return urlBasedCorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*"); // 출처
        corsConfiguration.addAllowedHeader("*"); // 헤더
        corsConfiguration.addAllowedMethod("*"); // 메서드

        urlBasedCorsConfigurationSource.registerCorsConfiguration("/api/*", corsConfiguration); // 특정 경로에 대한 cors 설정 등록

        return urlBasedCorsConfigurationSource;
    }

    /**
     * @description axios 요청 체크
     */
    private boolean isAxios(HttpServletRequest request) {
        return "axios".equals(request.getHeader("X-Request-Type"));
    }
}
