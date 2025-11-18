package com.peelie.friendship.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
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

    private FriendShipStage UserAtoBfriendStage; //각자의 퀴즈 현황에 따라 친구여도 퀴즈가 다를 수 있음
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
    public FriendShipStage getStageFor(Long viewerId) {
        if (viewerId.equals(senderId)) {
            return UserAtoBfriendStage;
        } else if (viewerId.equals(receiverId)) {
            return UserBtoAfriendStage;
        }
        throw new BaseException("해당 친구 관계에 속한 사용자가 아닙니다.", ErrorCode.VALIDATION_ERROR);
    }
}
