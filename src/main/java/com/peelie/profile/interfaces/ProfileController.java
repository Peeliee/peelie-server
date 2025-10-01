package com.peelie.profile.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.profile.application.ProfileFacade;
import com.peelie.profile.domain.ProfileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;

    @GetMapping
    public SuccessResponse getProfile(@RequestParam Long profileId) {
        ProfileInfo result = profileFacade.getProfile(profileId);
        return SuccessResponse.ok(result);
    }
}
