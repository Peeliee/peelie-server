package com.peelie.auth.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_accounts")
public class OauthAccount extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OauthProvider provider;

    @Column(nullable = false, unique = true)
    private String oid;

    private String email;

    private Long userId;

    @Builder
    public OauthAccount(OauthProvider provider, String oid, String email) {
        this.provider = provider;
        this.oid = oid;
        this.email = email;
    }

    public void linkUser(Long userId) {
        this.userId = userId;
    }
}
