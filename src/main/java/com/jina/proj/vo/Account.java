package com.jina.proj.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Table;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(appliesTo = "account", comment = "사용자정보")
public class Account implements UserDetails{
    
    @Comment("사용자_정보_테이블_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Comment("사용자_ID")
    @Column(nullable = false, length = 20)
    private String id;

    @Comment("사용자명")
    @Column(nullable = false, length = 20)
    private String name;

    @Comment("비밀번호")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;

    @Comment("등록_일자")
    @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime inDt;
    
    @Comment("잠김_여부")
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private String lckYn;

    @Comment("잠김_횟수")
    private int lckCnt;

    @Comment("잠금_일자")
    @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime lckDt;
    
    @Transient
    private Collection<? extends GrantedAuthority> role;

    public Long getIdx() {
        return this.idx;
    }

    public void setIdx(Long idx) {
        this.idx = idx;
    }
    
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public LocalDateTime getInDt() {
        return this.inDt;
    }

    public void setInDt(LocalDateTime inDt) {
        this.inDt = inDt;
    }

    public String getLckYn() {
        return this.lckYn;
    }

    public void setLckYn(String lckYn) {
        this.lckYn = lckYn;
    }

    public int getLckCnt() {
        return this.lckCnt;
    }

    public void setLckCnt(int lckCnt) {
        this.lckCnt = lckCnt;
    }

    public LocalDateTime getLckDt() {
        return this.lckDt;
    }

    public void setLckDt(LocalDateTime lckDt) {
        this.lckDt = lckDt;
    }

    public Collection<? extends GrantedAuthority> getRole() {
        return role;
    }

    public void setRole(Collection<? extends GrantedAuthority> role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }
    @Override
    public String getPassword() {
        return this.pwd;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    // 계정이 만료 되었는지 (true: 만료X)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겼는지 (true: 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return !this.lckYn.equals("Y");
    }

    // 비밀번호가 만료되었는지 (true: 만료X)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화(사용가능)인지 (true: 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
