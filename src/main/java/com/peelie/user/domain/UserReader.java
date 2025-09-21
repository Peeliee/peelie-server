package com.peelie.user.domain;

public interface UserReader {
    User getUser(String userToken);
    // 인증에서만 사용
    User getUserById(Long userId);
}
