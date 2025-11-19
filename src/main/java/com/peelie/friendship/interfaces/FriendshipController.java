package com.peelie.friendship.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.SuccessResponse;
import com.peelie.friendship.application.FriendshipFacade;
import com.peelie.friendship.domain.FriendshipInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipFacade friendshipFacade;

    // 친구 관계 즉시 형성
    @PostMapping
    public SuccessResponse createFriendship(@RequestBody FriendshipDto.CreateFriendshipRequest request) {
        Long senderId = UserContextHolder.getUserId();
        FriendshipInfo.FriendDetail detail = friendshipFacade.createFriendship(senderId, request.getUserId());

        return SuccessResponse.created(FriendshipDto.FriendDetailResponse.from(detail));
    }

    // 나의 친구 목록 조회
    @GetMapping
    public SuccessResponse getFriendList() {
        Long userId = UserContextHolder.getUserId();
        FriendshipInfo.FriendListResponse friendListResponse = friendshipFacade.getFriendList(userId);

        return SuccessResponse.ok(FriendshipDto.FriendListResponse.from(friendListResponse));
    }

    // 친구 상세 조회 (교류 단계가 포함되어있어야해서 나의 아이디 값도 필요함)
    @GetMapping("/{friendId}")
    public SuccessResponse getFriendDetail(@PathVariable("friendId") Long friendId) {
        Long userId = UserContextHolder.getUserId();
        FriendshipInfo.FriendDetail getFriendDetail =  friendshipFacade.getFriendDetail(userId, friendId);

        return SuccessResponse.ok(FriendshipDto.FriendDetailResponse.from(getFriendDetail));
    }

    // 랜덤 친구 5명 조회
    @GetMapping("/random")
    public SuccessResponse getRandomFriendList() {
        Long userId = UserContextHolder.getUserId();
        FriendshipInfo.RandomFriendResponse randomFriendResponse = friendshipFacade.getRandomFriend(userId);

        return SuccessResponse.ok(FriendshipDto.RandomFriendResponse.from(randomFriendResponse));
    }

}
