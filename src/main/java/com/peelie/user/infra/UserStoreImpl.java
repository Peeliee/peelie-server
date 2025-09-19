package com.peelie.user.infra;

import com.peelie.user.domain.User;
import com.peelie.user.domain.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStoreImpl implements UserStore {

    private final UserRepository userRepository;

    @Override
    public User store(User initUser) {
        return userRepository.save(initUser);
    }
}
