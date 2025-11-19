package com.peelie.profile.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.profile.interfaces.ProfileDto.RegisterProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Profile", description = "프로필 API 명세")
public interface ProfileDoc {

    @Operation(summary = "프로필 생성", description = "현재 로그인한 사용자의 프로필 생성")
    SuccessResponse registerProfile(@RequestBody RegisterProfileRequest info) ;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 조회")
    SuccessResponse getProfile();

    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필 수정")
    SuccessResponse updateProfile(@RequestBody ProfileDto.UpdateProfileRequest request);

    @Operation(summary = "프로필 사진 리셋", description = "현재 로그인한 사용자의 프로필 사진 리셋(나중에 쓰일 수도 있을 것 같아 일단 유지중임)")
    SuccessResponse resetProfileImage(@PathVariable Long profileId);
}

