package com.peelie.friendship.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.SuccessResponse;
import com.peelie.friendship.application.FriendshipFacade;
import com.peelie.friendship.domain.FriendshipInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friendship")
@RequiredArgsConstructor
public class FriendshipController implements FriendshipDoc {
    private final FriendshipFacade friendshipFacade;

    // 친구 관계 즉시 형성
    @PostMapping
    public SuccessResponse createFriendship(@RequestBody FriendshipDto.CreateFriendshipRequest request) {
        Long senderId = UserContextHolder.getUserId();
        FriendshipInfo.FriendDetail detail = friendshipFacade.createFriendship(senderId, request.getUserId());

        return SuccessResponse.created(FriendshipDto.FriendDetail.from(detail));
    }

    // 나의 친구 목록 조회
    @GetMapping
    public SuccessResponse<List<FriendshipDto.FriendList>> getFriendList() {
        Long userId = UserContextHolder.getUserId();
        FriendshipInfo.FriendListResponse info = friendshipFacade.getFriendList(userId);

        return SuccessResponse.ok(FriendshipDto.toListItems(info.getItems()));
    }

    // 친구 상세 조회 (교류 단계가 포함되어있어야해서 나의 아이디 값도 필요함)
    @GetMapping("/{friendId}")
    public SuccessResponse getFriendDetail(@PathVariable("friendId") Long friendId) {
        Long userId = UserContextHolder.getUserId();
        FriendshipInfo.FriendDetail getFriendDetail =  friendshipFacade.getFriendDetail(userId, friendId);

        return SuccessResponse.ok(FriendshipDto.FriendDetail.from(getFriendDetail));
    }

    @GetMapping("/{userId}/exists")
    public SuccessResponse<FriendshipDto.ExistsResponse> checkFriendship(@PathVariable("userId") Long targetUserId) {
        Long userId = UserContextHolder.getUserId();
        boolean exists = friendshipFacade.existsFriendship(userId, targetUserId);
        return SuccessResponse.ok(FriendshipDto.ExistsResponse.from(exists));
    }

    // 랜덤 친구 5명 조회
    @GetMapping("/random")
    public SuccessResponse<List<FriendshipDto.FriendList>> getRandomFriendList() {
        Long userId = UserContextHolder.getUserId();
        FriendshipInfo.RandomFriendResponse info = friendshipFacade.getRandomFriend(userId);

        return SuccessResponse.ok(FriendshipDto.toListItems(info.getItems()));
    }

}
