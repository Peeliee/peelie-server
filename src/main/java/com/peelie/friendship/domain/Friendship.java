package com.peelie.friendship.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long senderId;
    private Long receiverId;

    // 스테이지 이넘값
    @Enumerated(EnumType.STRING)

    private FriendStage UserAfriendStage;
    private FriendStage UserBfriendStage;



    @Builder
    public Friendship(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.UserAfriendStage = FriendStage.STAGE_0;
        this.UserBfriendStage = FriendStage.STAGE_0;
    }

//    public void changeFriendStage(FriendStage newfriendStage) {
//        this.friendStage = Objects.requireNonNull(newfriendStage);
//    }
}

