package com.peelie.auth.domain;


import java.util.Optional;

public interface OauthReader {
    Optional<OauthAccount> findByProviderAndOid(OauthProvider provider, String oid);
    OauthAccount getOauthAccount(Long oauthAccountId); //
}
