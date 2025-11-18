package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import com.peelie.friendship.domain.FriendshipReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendshipReaderImpl implements FriendshipReader {

    private final FriendshipRepository friendshipRepository;

    @Override
    public List<Long> findFriendsByUserId(Long userId) {
        return friendshipRepository.findFriendsByUserId(userId);
    }

    @Override
    public boolean existPair(Long a, Long b) {
        Long user1 = Math.min(a, b);
        Long user2 = Math.max(a, b);

        return friendshipRepository.existsByUserId1AndUserId2(user1, user2);
    }

    @Override
    public Friendship getByPair(Long a, Long b) {
        Long user1 = Math.min(a, b);
        Long user2 = Math.max(a, b);

        return friendshipRepository.findBySenderIdAndReceiverId(user1, user2);
    }
}
