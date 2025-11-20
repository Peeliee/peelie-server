package com.peelie.friendship.domain;

public interface FriendshipService {
    FriendshipInfo.FriendDetail createFriendship(Long senderId, Long receiverId);
    FriendshipInfo.FriendListResponse getFriendList(Long senderId);
    FriendshipInfo.FriendDetail  getFriendDetail(Long senderId, Long receiverId);
    boolean existsFriendship(Long userId, Long receiverId);
    FriendshipInfo.RandomFriendResponse  getRandomFriend(Long userId);
}
