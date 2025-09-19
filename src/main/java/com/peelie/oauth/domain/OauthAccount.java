package com.peelie.oauth.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.common.util.TokenGenerator;
import com.peelie.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private String email;

    // User 연결 여부
    @Column(nullable = false)
    private boolean linked;

    @Builder
    public OauthAccount(OauthProvider provider, String oid, String email) {
        this.oauthAccountToken = TokenGenerator.randomCharacterWithPrefix(PREFIX_OAUTH);
        this.provider = provider;
        this.oid = oid;
        this.email = email;
        this.linked = false; //생성시점엔 User와 연결되지 않음
    }

    // User와 연결하고, linked 상태 변경
    public void linkUser(User user) {
        this.user = user;
        this.linked = true;
    }
}
