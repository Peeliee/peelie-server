package com.peelie.friendship.domain;

public interface FriendshipService {
    FriendshipInfo.FriendDetail createFriendship(Long receiverId, Long senderId);
    FriendshipInfo.FriendListResponse getFriendList(Long senderId);
    FriendshipInfo.FriendDetail  getFriendDetail(Long senderId, Long receiverIdd);
    FriendshipInfo.RandomFriendResponse  getRandomFriend(Long userId);
}
