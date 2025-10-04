package com.peelie.profile.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.profile.application.ProfileFacade;
import com.peelie.profile.domain.ProfileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.peelie.profile.domain.ProfileService;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;

    public record RegisterProfileRequest(Long userId, String userName, String instagramId, String imageUrl) {}
    public record UpdateNameRequest(Long userId, String newProfileName) {}
    public record UpdateInstagramRequest(Long userId, String newInstagramId) {}
    public record UpdateImageUrlRequest(Long userId, String newImgUrl) {}

    //Todo: 모든 API 파라미터 DTO로 수정 (완료)

    // 프로필 생성
    @PostMapping
    public SuccessResponse registerProfile(@RequestBody RegisterProfileRequest info) {
        ProfileInfo profileInfo = profileFacade.registerProfile(
                info.userId, info.userName, info.instagramId, info.imageUrl
        );
        return SuccessResponse.created(profileInfo);
    }

    // 프로필 조회
    @GetMapping
    public SuccessResponse getProfile(@RequestParam Long profileId) {
        ProfileInfo result = profileFacade.getProfile(profileId);
        return SuccessResponse.ok(result);
    }

    // 프로필 수정
    // 프로필 이름 수정
    @PatchMapping("/{userId}/name")
    public SuccessResponse updateProfileName(@RequestParam UpdateNameRequest info) {
        profileFacade.updateProfileName(info.userId, info.newProfileName);
        return SuccessResponse.ok(null);
    }

    // 프로필 인스타그램 아이디 수정
    @PatchMapping("/{userId}/instagram")
    public SuccessResponse updateInstagramId(@RequestParam UpdateInstagramRequest info) {
        profileFacade.updateInstagramId(info.userId, info.newInstagramId);
        return SuccessResponse.ok(null);
    }

    //프로필 사진 수정
    @PatchMapping("/{userId}/image")
    public SuccessResponse updateImgUrl(@RequestParam UpdateImageUrlRequest info) {
        profileFacade.updateImageUrl(info.userId, info.newImgUrl);
        return SuccessResponse.ok(null);
    }

    // 프로필 사진 리셋
    @PatchMapping("/{userId}/reset}")
    public SuccessResponse resetProfileImage(@RequestParam Long userId) {
        profileFacade.resetProfileImage(userId);
        return SuccessResponse.ok(null);
    }
}
