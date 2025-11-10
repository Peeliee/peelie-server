package com.peelie.friendship.domain;

public interface FriendshipService {
    FriendshipInfo.CreateFriendship createFriendship(Long receiverId);
    FriendshipInfo.FriendListResponse getFriendList(Long userid);
    FriendshipInfo.GetFriendDetail  getFriendDetail(Long userid);
    FriendshipInfo.RandomFriendResponse  getRandomFriend(Long userid);
}
