package com.peelie.user.domain;

public interface UserService {
    UserInfo registerUser();
    UserInfo getUserInfo(Long userId);
}
