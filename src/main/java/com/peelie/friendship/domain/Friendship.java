package com.peelie.friendship.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long senderId; // User 1
    private Long receiverId; // User 2

    // 스테이지 이넘값
    @Enumerated(EnumType.STRING)

    private FriendShipStage UserAtoBfriendStage;
    private FriendShipStage UserBtoAfriendStage;

    @Builder
    public Friendship(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.UserAtoBfriendStage = FriendShipStage.STAGE_0;
        this.UserBtoAfriendStage = FriendShipStage.STAGE_0;
    }

    public void changeFriendStage(Long viewerId, FriendShipStage newFriendshipStage) {
        if (viewerId.equals(senderId)) {
            this.UserAtoBfriendStage = newFriendshipStage;
        } else if (viewerId.equals(receiverId)) {
            this.UserBtoAfriendStage = newFriendshipStage;
        }
    }
}
