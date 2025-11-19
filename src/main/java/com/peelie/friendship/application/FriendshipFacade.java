package com.peelie.friendship.application;


import com.peelie.friendship.domain.FriendshipInfo;
import com.peelie.friendship.domain.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendshipFacade {

    private final FriendshipService friendshipService;

    // 친구 생성
    public FriendshipInfo.FriendDetail createFriendship(Long senderId, Long receiverId) {
        return friendshipService.createFriendship(senderId, receiverId);
    }

    // 나의 친구 목록 조회
    public FriendshipInfo.FriendListResponse getFriendList(Long userId) {
        return friendshipService.getFriendList(userId);
    }

    // 친구 상세 조회
    public FriendshipInfo.FriendDetail getFriendDetail(Long senderId, Long receiverId) {
        return friendshipService.getFriendDetail(senderId, receiverId);
    }

    // 랜덤 친구 5명 조회
    public FriendshipInfo.RandomFriendResponse getRandomFriend(Long userId) {
        return friendshipService.getRandomFriend(userId);
    }
}
