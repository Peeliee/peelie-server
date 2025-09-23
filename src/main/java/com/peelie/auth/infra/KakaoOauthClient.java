package com.peelie.auth.infra;

import com.peelie.auth.domain.OauthClient;
import com.peelie.auth.domain.OauthProvider;
import com.peelie.auth.domain.OauthResourceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOauthClient implements OauthClient {

    private final RestClient restClient;
    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;
    @Value("${kakao.token-uri}")
    private String tokenUri;
    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Override
    public OauthProvider getProvider() {
        return OauthProvider.KAKAO;
    }

    @Override
    public OauthResourceResponse fetchProfile(String code) {
        String accessToken = requestAccessToken(code);
        Map<String, Object> responseBody = restClient.get().uri(userInfoUri).header("Authorization", "Bearer " + accessToken).retrieve().body(Map.class);
        Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
        String oid = responseBody.get("id").toString();

        return OauthResourceResponse.builder()
                .provider(OauthProvider.KAKAO)
                .oid(oid)
                .email((String) kakaoAccount.get("email"))
                .build();
    }

    private String requestAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        Map<String, Object> response = restClient.post().uri(tokenUri).body(body).retrieve().body(Map.class);
        return (String) response.get("access_token");
    }
}