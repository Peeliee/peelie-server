package com.peelie.friendship.domain;

public interface FriendshipService {
    FriendshipInfo.FriendDetail createFriendship(Long receiverId, Long senderId);
    FriendshipInfo.FriendListResponse getFriendList(Long userId);
    FriendshipInfo.FriendDetail  getFriendDetail(Long userId);
    FriendshipInfo.RandomFriendResponse  getRandomFriend(Long userId);
}
