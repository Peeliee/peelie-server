package com.peelie.friendship.infra;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
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
        List<Friendship> friendships = friendshipRepository.findFriendsBySenderIdOrReceiverId(userId, userId);
        return friendships.stream()
                .map(f -> f.getSenderId().equals(userId)
                        ? f.getReceiverId()
                        : f.getSenderId())
                .toList();
    }

    @Override
    public boolean existPair(Long a, Long b) {
        Long user1 = Math.min(a, b);
        Long user2 = Math.max(a, b);

        return friendshipRepository.existsBySenderIdAndReceiverId(user1, user2);
    }

    @Override
    public Friendship getByPair(Long a, Long b) {
        Long user1 = Math.min(a, b);
        Long user2 = Math.max(a, b);

        return friendshipRepository.findBySenderIdAndReceiverId(user1, user2)
                .orElseThrow(() -> new BaseException("친구관계가 존재하지 않습니다.", ErrorCode.VALIDATION_ERROR));
    }
}
