package com.peelie.friendship.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class FriendshipCommand {

    @Getter
    @Builder
    @ToString
    public static class CreateFriendship {
        Long receiverId;
    }

    @Getter
    @Builder
    @ToString
    public static class GetFriendList {
    }

    @Getter
    @Builder
    @ToString
    public static class getFriendDetail {
    }

    @Getter
    @Builder
    @ToString
    public static class getRandomFriend {
    }

}
