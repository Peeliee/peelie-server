package com.peelie.friendship.infra;

import com.peelie.friendship.domain.FriendshipReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendshipReaderImpl implements FriendshipReader {

    private final FriendshipRepository friendshipRepository;

    @Override
    public List<Long> findFriendByUserIds(Long userId) {
        return friendshipRepository.getFriendshipById(userId);
    }

    @Override
    public boolean existPair(Long a, Long b) {
        Long user1 = Math.min(a, b);
        Long user2 = Math.max(a, b);

        return friendshipRepository.existsByUserId1AndUserId2(user1, user2);
    }
}
