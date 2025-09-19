package com.peelie.oauth.infra;

import com.peelie.oauth.domain.OauthAccount;
import com.peelie.oauth.domain.OauthStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthStoreImpl implements OauthStore {

    private final OauthRepository oauthRepository;

    @Override
    public OauthAccount store(OauthAccount oauthAccount) {
        return oauthRepository.save(oauthAccount);
    }
}
