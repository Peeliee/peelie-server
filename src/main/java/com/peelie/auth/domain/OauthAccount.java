package com.peelie.auth.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.common.util.TokenGenerator;
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
    private static final String PREFIX_OAUTH = "oac_"; // Oauth Account

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String oauthAccountToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OauthProvider provider;

    @Column(nullable = false, unique = true)
    private String oid;

    private String email;

    private Long userId;

    // User 연결 여부
    @Column(nullable = false)
    private boolean linked;

    @Builder
    public OauthAccount(OauthProvider provider, String oid, String email) {
        this.oauthAccountToken = TokenGenerator.randomCharacterWithPrefix(PREFIX_OAUTH);
        this.provider = provider;
        this.oid = oid;
        this.email = email;
        this.linked = false; //생성시점엔 User 연결되지 않음
    }

    public void linkUser(Long userId) {
        this.userId = userId;
        this.linked = true;
    }
}
