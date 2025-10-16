package com.peelie.profile.interfaces;

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
        ProfileCommand command = info.toCommand();
        ProfileInfo profileInfo = profileFacade.registerProfile(command);
        return SuccessResponse.created(profileInfo);
    }

    // 프로필 조회
    @GetMapping("/{profileId}")
    public SuccessResponse getProfile(@PathVariable Long profileId) {
        ProfileInfo result = profileFacade.getProfile(profileId);
        return SuccessResponse.ok(result);
    }

    // 프로필 수정
    // 프로필 이름 수정
    @PatchMapping("/name")
    public SuccessResponse updateProfileName(@RequestBody UpdateProfileNameRequest info) {
        profileFacade.updateProfileName(
                info.userId(), info.newName()
        );
        return SuccessResponse.ok(null);
    }

    // 프로필 인스타그램 아이디 수정
    @PatchMapping("/instagram")
    public SuccessResponse updateInstagramId(@RequestBody UpdateInstagramRequest info) {
        profileFacade.updateInstagramId(
                info.userId(), info.newInstagramId()
        );
        return SuccessResponse.ok(null);
    }

    //프로필 사진 수정
    @PatchMapping("/image")
    public SuccessResponse updateImgUrl(@RequestBody UpdateImageUrlRequest info) {
        profileFacade.updateImageUrl(
                info.userId(), info.newImgUrl()
        );
        return SuccessResponse.ok(null);
    }

    // 프로필 사진 리셋
    @PatchMapping("/{profileId}/resetimage")
    public SuccessResponse resetProfileImage(@PathVariable Long profileId) {
        profileFacade.resetProfileImage(profileId);
        return SuccessResponse.ok(null);
    }
}
