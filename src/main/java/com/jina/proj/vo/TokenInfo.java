package com.jina.proj.vo;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @description 토큰 관리 엔티티(VO) 클래스
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(appliesTo = "token_info", comment = "토큰_정보")
public class TokenInfo {

    @Comment("사용자_ID")
    @Id
    private String usrId;
    @Comment("리프래쉬_토큰")
    @Column(nullable = false)
    private String refsToken;

    @Transient
    private String accToken;

    @Transient
    private String cntnRslt;
}
