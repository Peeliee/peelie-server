package com.peelie.auth.interfaces;

import com.peelie.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "oauth", description = "oauth 로그인 명세")
public interface OauthDoc {

    @Operation(summary = "oauth 로그인", description = "현재는 카카오 로그인")
    SuccessResponse login(
            @Parameter(
                    name = "provider",
                    description = "oauth 제공자 (지금은 kakao 고정)",
                    example = "kakao"
            )
            @PathVariable String provider,
            @Parameter(name = "oauth 인증 코드")
            @RequestParam String code);
}
