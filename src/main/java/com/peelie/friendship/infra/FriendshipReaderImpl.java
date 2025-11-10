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
    public List<Long> findFriendUserIds(Long userId) {
        return friendshipRepository.getFriendshipById(userId);
    }
}
