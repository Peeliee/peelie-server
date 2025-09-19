package com.peelie.user.infra;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.user.domain.User;
import com.peelie.user.domain.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {

    private final UserRepository userRepository;

    @Override
    public User getUser(String userToken) {
        return userRepository.findByUserToken(userToken)
                .orElseThrow(() -> new BaseException("해당 유저가 없습니다.", ErrorCode.NOT_FOUND));
    }
}
