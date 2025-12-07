package com.peelie.friendship.interfaces;

import com.peelie.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Friendship", description = "친구 관계 API 명세")

public interface FriendshipDoc {

    @Operation(summary = "친구 관계 형성", description = "현재 로그인한 사용자와 대상 사용자 간의 친구 관계를 즉시 형성")
    SuccessResponse createFriendship(@RequestBody FriendshipDto.CreateFriendshipRequest request);

    @Operation(summary = "친구 목록 조회", description = "현재 로그인한 사용자의 친구 목록 조회")
    SuccessResponse<List<FriendshipDto.FriendList>> getFriendList();

    @Operation(summary = "친구 상세 조회", description = "특정 친구의 상세 정보 조회 (교류 단계에 따라 공개 정보가 다름)")
    SuccessResponse getFriendDetail(@PathVariable("friendId") Long friendId);

    @Operation(summary = "친구 존재 여부 확인", description = "현재 로그인한 사용자와 대상 사용자 간의 친구 관계 존재 여부 확인")
    SuccessResponse<FriendshipDto.ExistsResponse> checkFriendship(@PathVariable("userId") Long targetUserId);

    @Operation(summary = "랜덤 친구 조회", description = "STAGE_3가 아닌 친구 중 랜덤으로 최대 5명 조회 (24시간 동안 동일한 결과)")
    SuccessResponse<List<FriendshipDto.FriendList>> getRandomFriendList();

}