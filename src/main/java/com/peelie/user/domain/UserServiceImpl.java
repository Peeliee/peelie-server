package com.peelie.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStore userStore;
    private final UserReader userReader;

    @Override
    @Transactional
    public UserInfo registerUser() {
        User initUser = User.create();
        User user = userStore.store(initUser);
        return new UserInfo(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(String userToken) {
        User user = userReader.getUser(userToken);
        return new UserInfo(user);
    }
}
