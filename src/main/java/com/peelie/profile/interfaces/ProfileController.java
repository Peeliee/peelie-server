package com.peelie.profile.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.SuccessResponse;
import com.peelie.profile.application.ProfileFacade;
import com.peelie.profile.domain.ProfileCommand;
import com.peelie.profile.domain.ProfileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.peelie.profile.interfaces.ProfileDto.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController implements ProfileDoc{
    //TODO: profileInfo 직접 반환 말고 적절한 DTO 생성해서 반환하기

    private final ProfileFacade profileFacade;

    // 프로필 생성
    @PostMapping
    public SuccessResponse registerProfile(@RequestBody RegisterProfileRequest info) {
        Long userId = UserContextHolder.getUserId();
        ProfileCommand.RegisterCommand command = info.toCommand(userId);
        ProfileInfo profileInfo = profileFacade.registerProfile(command);
        return SuccessResponse.created(profileInfo);
    }

    // 프로필 조회
    @GetMapping
    public SuccessResponse getProfile() {
        Long userId = UserContextHolder.getUserId();
        ProfileInfo result = profileFacade.getMyProfile(userId);
        return SuccessResponse.ok(result);
    }

    // 프로필 수정
    @PatchMapping
    public SuccessResponse updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = UserContextHolder.getUserId();
        ProfileCommand.UpdateCommand command = request.toCommand();
        ProfileInfo profileInfo = profileFacade.updateProfile(userId, command);
        return SuccessResponse.ok(profileInfo);
    }

    // 프로필 사진 리셋
    @PatchMapping("/{profileId}/resetimage")
    public SuccessResponse resetProfileImage(@PathVariable Long profileId) {
        profileFacade.resetProfileImage(profileId);
        return SuccessResponse.ok(null);
    }
}
