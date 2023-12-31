package com.jina.proj.usr.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jina.proj.vo.TokenInfo;

import java.util.Optional;

/**
 * @description 토큰 정보 repository
 * @packageName com.egiskorea.usr.login.repository
 * @class TokenRepository.java
 * @version 1.0
 * @see
 *
 * << 개정이력(Modification Information) >>
 * 수정일        수정자          수정내용
 * ----------   --------   ---------------------------
 *
 */

@Repository
public interface TokenRepository extends JpaRepository<TokenInfo, String> {

    /**
     * @description 토큰 정보 조회
     * @param usrId 사용자 ID
     * @return Optional<TokenInfo> 토큰 정보
     */
    Optional<TokenInfo> findByUsrId(String usrId);
}
