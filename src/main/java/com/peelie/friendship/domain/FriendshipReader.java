package com.peelie.friendship.domain;

import java.util.List;

public interface FriendshipReader {
    List<Long> findFriendsByUserId(Long userId);
    boolean existPair(Long a, Long b);
}
