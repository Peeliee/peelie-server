package com.peelie.auth.domain;

public interface OauthStore {
    OauthAccount store(OauthAccount oauthAccount);
}
