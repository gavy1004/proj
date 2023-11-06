package com.jina.proj.config.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
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

@Slf4j  // 로그 자동 생성 (lombok)
@Configuration
@RequiredArgsConstructor // final 생성자 자동 생성(lombok)
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
        return (web) -> web.ignoring().antMatchers( // 특정 요청 패턴 무시하도록 설정
                "/css/**",
                "/images/**",
                "/plugins/**",
                "/js/**",
                "/login",
                "/join",
                "/logout",
                "/",
                "/favicon.ico",
                "/error",
                "/usr/**"
        );
    }

    /**
     * @description HttpSecurity 구성  
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors() // CORS 필터 적용
            .and()
            .csrf().disable() // CSRF 보호 기능 비활성화
            .httpBasic().disable() // http 기본 인증 비활성화
            .formLogin().disable() // 폼 로그인 비활성화

            .headers() // http 응답 헤더 구성
            .frameOptions()
            .sameOrigin() // 동일 출처 프레임 사용 허용

            .and()
            .sessionManagement() // 세션 관리 설정
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 생성하지 않음

            // 권한 검사
            .and()
            .authorizeRequests()    // authorizeRequest() : 인증, 인가가 필요한 URL 지정
            .antMatchers(           // antMatchers(URL)
                    "/main"
            ).authenticated()       // 해당 URL에 진입하기 위해서 Authentication(인증, 로그인)이 필요함
                                    // 해당 URL에 진입하기 위해서 Authorization(인가, ex)권한이 ADMIN인 유저만 진입 가능)이 필요함
            .anyRequest()           // anyRequest() : 그 외의 모든 URL
            //.access("@JwtAuthChecker.checkAuthURI(request)") // 권한별 메뉴 접근 허용
            .authenticated()
            /* 
            //.addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class) // JWT*/
            .and()
            // 예외 핸들링 추가
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint()) // 인증
            .accessDeniedHandler(accessDeniedHandler()); // 인가

        return httpSecurity.build();
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
