package com.jina.proj.usr.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jina.proj.vo.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

    // And Or Is Equals Between
    public Optional<Account> findByUserId(String id);

} 