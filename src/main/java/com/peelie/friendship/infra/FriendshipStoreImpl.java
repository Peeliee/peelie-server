package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import com.peelie.friendship.domain.FriendshipStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipStoreImpl implements FriendshipStore {

    private final FriendshipRepository friendshipRepository;

    @Override
    public Friendship store(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }
}
