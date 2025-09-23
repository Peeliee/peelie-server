package com.peelie.auth.infra;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.auth.domain.OauthAccount;
import com.peelie.auth.domain.OauthProvider;
import com.peelie.auth.domain.OauthReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OauthReaderImpl implements OauthReader {

    private final OauthRepository oauthRepository;

    @Override
    public Optional<OauthAccount> findByProviderAndOid(OauthProvider provider, String oid) {
        return oauthRepository.findByProviderAndOid(provider, oid);
    }

    @Override
    public OauthAccount getOauthAccount(Long oauthAccountId) {
        return oauthRepository.findById(oauthAccountId)
                .orElseThrow(() -> new BaseException("해당 토큰에 해당하는 정보가 없습니다.", ErrorCode.NOT_FOUND));
    }
}
