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
public class ProfileController {

    private final ProfileFacade profileFacade;

    // 프로필 생성
    @PostMapping
    public SuccessResponse registerProfile(@RequestBody RegisterProfileRequest info) {
        Long userId = UserContextHolder.getUserId();
        ProfileCommand command = info.toCommand(userId);
        ProfileInfo profileInfo = profileFacade.registerProfile(command);
        return SuccessResponse.created(profileInfo);
    }

    // 프로필 조회
    @GetMapping
    public SuccessResponse getProfile() {
        Long userId = UserContextHolder.getUserId();
        ProfileInfo result = profileFacade.getProfile(userId);
        return SuccessResponse.ok(result);
    }

    // 프로필 수정
    // 프로필 이름 수정
    @PatchMapping("/name")
    public SuccessResponse updateProfileName(@RequestBody UpdateProfileNameRequest info) {
        Long userId = UserContextHolder.getUserId();
        profileFacade.updateProfileName(userId, info.newName());
        return SuccessResponse.ok(null);
    }

    // 프로필 인스타그램 아이디 수정
    @PatchMapping("/instagram")
    public SuccessResponse updateInstagramId(@RequestBody UpdateInstagramRequest info) {
        Long userId = UserContextHolder.getUserId();
        profileFacade.updateInstagramId(userId, info.newInstagramId());
        return SuccessResponse.ok(null);
    }

    //프로필 사진 수정
    @PatchMapping("/image")
    public SuccessResponse updateImgUrl(@RequestBody UpdateImageUrlRequest info) {
        Long userId = UserContextHolder.getUserId();
        profileFacade.updateImageUrl(userId, info.newImgUrl());
        return SuccessResponse.ok(null);
    }

    // 프로필 사진 리셋
    @PatchMapping("/{profileId}/resetimage")
    public SuccessResponse resetProfileImage(@PathVariable Long profileId) {
        profileFacade.resetProfileImage(profileId);
        return SuccessResponse.ok(null);
    }
}
