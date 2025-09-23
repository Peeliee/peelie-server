package com.peelie.auth.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.auth.application.LoginResponse;
import com.peelie.auth.application.OauthFacade;
import com.peelie.auth.domain.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OauthController {

    private final OauthFacade oauthFacade;

    @PostMapping("/login/{provider}")
    public SuccessResponse login(@PathVariable String provider, @RequestParam String code) {
        OauthProvider oauthProvider = OauthProvider.valueOf(provider.toUpperCase());
        LoginResponse response = oauthFacade.login(oauthProvider, code);
        return SuccessResponse.ok(response);
    }
}