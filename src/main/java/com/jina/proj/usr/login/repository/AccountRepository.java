package com.jina.proj.usr.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jina.proj.vo.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

    // findBy뒤에 컬럼명을 붙여주면 이를 이용한 검색이 가능하다

    // And Or Is Equals Between
    public Optional<Account> findByUserId(String id);

} 