package com.peelie.oauth.infra;

import com.peelie.oauth.domain.OauthAccount;
import com.peelie.oauth.domain.OauthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthRepository extends JpaRepository<OauthAccount, Long> {
    Optional<OauthAccount> findByProviderAndOid(OauthProvider provider, String oid);
    Optional<OauthAccount> findByOauthAccountToken(String token);
}
