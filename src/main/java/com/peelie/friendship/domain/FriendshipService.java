package com.peelie.friendship.domain;

public interface FriendshipService {
    FriendshipInfo.FriendDetail createFriendship(Long receiverId, Long senderId);
    FriendshipInfo.FriendListResponse getFriendList(Long userid);
    FriendshipInfo.FriendDetail  getFriendDetail(Long userid);
    FriendshipInfo.RandomFriendResponse  getRandomFriend(Long userid);
}
