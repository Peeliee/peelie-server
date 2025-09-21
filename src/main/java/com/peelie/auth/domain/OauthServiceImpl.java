package com.peelie.auth.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.user.domain.User;
import com.peelie.user.domain.UserReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OauthServiceImpl implements OauthService {

    private final UserReader userReader;
    private final OauthReader oauthReader;
    private final OauthStore oauthStore;
    private final Map<OauthProvider, OauthClient> clientMap;

    public OauthServiceImpl(OauthReader oauthReader,
                            OauthStore oauthStore,
                            UserReader userReader,
                            List<OauthClient> clients) {
        this.oauthReader = oauthReader;
        this.oauthStore = oauthStore;
        this.userReader = userReader;
        this.clientMap = clients.stream()
                .collect(Collectors.toUnmodifiableMap(OauthClient::getProvider, Function.identity()));
    }

    @Override
    @Transactional
    public OauthInfo authenticate(OauthProvider provider, String code) {
        OauthClient client = clientMap.get(provider);
        OauthResourceResponse oauthResource = client.fetchProfile(code);

        // OauthAccount가 있으면 반환, 없으면 새로 생성해서 반환
        OauthAccount oauthAccount = oauthReader.findByProviderAndOid(provider, oauthResource.getOid())
                .orElseGet(() -> oauthStore.store(
                        OauthAccount.builder()
                                .provider(oauthResource.getProvider())
                                .oid(oauthResource.getOid())
                                .email(oauthResource.getEmail())
                                .build()
                ));

        return new OauthInfo(oauthAccount);
    }

    @Override
    @Transactional
    public OauthInfo linkUser(String oauthAccountToken, Long userId) {

        OauthAccount oauthAccount = oauthReader.getOauthAccountByToken(oauthAccountToken);

        if (oauthAccount.isLinked()) {
            throw new BaseException("이미 등록된 사용자입니다.", ErrorCode.VALIDATION_ERROR);
        }

        oauthAccount.linkUser(userId);

        return new OauthInfo(oauthAccount);
    }

    @Override
    public String getUserToken(Long userId) {
        User user = userReader.getUserById(userId);
        return user.getUserToken();
    }
}
