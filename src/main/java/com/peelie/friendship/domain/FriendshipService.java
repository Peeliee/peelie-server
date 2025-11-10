package com.peelie.friendship.domain;

public interface FriendshipService {
    FriendshipInfo.CreateFriendship createFriendship(FriendshipCommand.CreateFriendship command);
    FriendshipInfo.GetFriendList getFriendList(FriendshipCommand.GetFriendList command);
}
