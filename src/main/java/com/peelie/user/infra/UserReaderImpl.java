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
                .orElseThrow(() -> new BaseException("해당 토큰 유저가 없습니다.", ErrorCode.NOT_FOUND));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException("해당 id 유저가 없습니다.", ErrorCode.NOT_FOUND));
    }
}
