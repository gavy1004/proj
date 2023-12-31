package com.jina.proj.usr.login.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jina.proj.config.security.JwtProvider;
import com.jina.proj.usr.login.repository.AccountRepository;
import com.jina.proj.usr.login.repository.TokenRepository;
import com.jina.proj.usr.login.service.LoginService;
import com.jina.proj.vo.Account;
import com.jina.proj.vo.TokenInfo;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service(value = "loginService")
public class LoginServiceImpl implements LoginService, UserDetailsService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;

    /**
     * @methodName login
     * @description 로그인
     */
    @Override
    public void login(Account account, HttpServletRequest request, HttpServletResponse response) throws Exception {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account.getUserId(), account.getUserPwd()); // 인증 객체 생성
            
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); // loadUserByUsername 메소드를 통해 검증
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder에 인증 정보 저장
            TokenInfo token = jwtProvider.createToken(authentication); // 인증 정보를 기반으로 JWT 토큰 생성
            Claims claims = jwtProvider.parseClaims(token.getAccToken()); // JWT 토큰을 파싱하여 JWT payload 에 저장된 정보

            // 사용자 정보 조회
            Account user = accountRepository.findByUserId(account.getUserId())
                    .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호를 잘못 입력하셨습니다."));
            user.setLckCnt(0); //잠김 횟수 초기화
            accountRepository.save(user); // 사용자 정보 저장

            // 토큰 정보 엔티티 생성
            TokenInfo tokenInfo = TokenInfo.builder()
                    .usrId(claims.getSubject())
                    .refsToken(token.getRefsToken())
                    .build();
            tokenRepository.save(tokenInfo); // 토큰 정보 DB에 저장
            jwtProvider.setCookie(token, response); // 토큰 쿠키에 저장
            
        }catch (BadCredentialsException e) { // 아이디 또는 비밀번호 틀린 경우 예외 처리

            // 사용자 정보 조회
            Account user = accountRepository.findByUserId(account.getUserId())
                    .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호를 잘못 입력하셨습니다."));
            user.setLckCnt(user.getLckCnt() + 1); // 잠김 횟수 증가

            // 잠김 횟수 5되면 계정 잠금 및 예외 처리
            if (user.getLckCnt() >= 5) {
                user.setLckYn("Y");
                throw new LockedException("비밀번호를 여러 번 잘못 입력하여 계정이 잠금되었습니다. \n계정 잠금을 해제하려면 관리자에 문의하세요.");
            }

            accountRepository.save(user);

            throw new BadCredentialsException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
        } catch (LockedException e) { // 계정 잠금 예외
            throw new LockedException("비밀번호를 여러 번 잘못 입력하여 계정이 잠금되었습니다. \n계정 잠금을 해제하려면 관리자에 문의하세요.");
        } catch (DisabledException e){
            throw new DisabledException("계정이 가입 승인 대기 중입니다. \n가입 승인 후 로그인하세요.");
        } catch (RuntimeException e) {
            throw new RuntimeException("오류가 발생하였습니다.");
        }
    }
    /**
     * @methodName logout
     * @description 로그아웃
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accToken = jwtProvider.getCookie(request); // 쿠키에서 엑세스 토큰을 가져온다.
            Claims claims = jwtProvider.parseClaims(accToken); // 토큰에서 payload 정보(사용자 정보)를 가져온다.

            // 쿠키 삭제
            Cookie cookie = new Cookie("token", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            tokenRepository.deleteById(claims.getSubject()); // DB에서 토큰 삭제
            Authentication authentication = jwtProvider.getAuthentication(accToken); // 토큰에서 권한 정보를 가져온다.
            //String usrId = jwtProvider.parseUsrId(authentication.getName()); // 사용자 ID
            SecurityContextHolder.clearContext(); // SecurityContextHolder 초기화

            log.error("로그아웃에 성공하였습니다.");
        } catch (Exception e) {
            log.error("로그아웃에 실패하였습니다.");
        }
    }

    /**
     * @methodName join
     * @description 회원가입
     */
    @Override
    public void join(Account account) throws Exception {
        try{
            account.setInDt(LocalDateTime.now());
            account.setLckYn("N");
            account.setLckDt(null);
            account.setAuthCd("U");
            account.setUserPwd(passwordEncoder.encode(account.getUserPwd()));
            accountRepository.save(account);
        }catch(Exception e){
            throw new Exception("회원가입에 실패하였습니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserId(username)
                            .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호를 잘못 입력하셨습니다."));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(account.getAuthCd()));    // 권한 추가
        account.setRole(authorities); 

        return account;
    }

}
