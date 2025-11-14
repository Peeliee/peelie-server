package com.peelie.friendship.domain;

import java.util.List;

public interface FriendshipReader {
    List<Long> findFriendByUserIds(Long userId);

    boolean existPair(Long a, Long b);
}
