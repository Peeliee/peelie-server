package com.peelie.friendship.domain;

public interface x {
    FriendshipInfo.CreateFriendship createFriendship(FriendshipCommand.CreateFriendship command);
    FriendshipInfo.GetFriendList getFriendList(FriendshipCommand.GetFriendList command);
}
