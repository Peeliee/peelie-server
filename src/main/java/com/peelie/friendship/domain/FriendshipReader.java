package com.peelie.friendship.domain;

import java.util.List;

public interface FriendshipReader {
    List<Long> findFriendUserIds(Long userId);
}
