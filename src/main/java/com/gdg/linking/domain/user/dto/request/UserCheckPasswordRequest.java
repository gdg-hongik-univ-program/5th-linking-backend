package com.gdg.linking.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "설정창 진입 전 비밀번호 확인 요청")
public class UserCheckPasswordRequest {

    @Schema(description = "확인할 비밀번호", example = "password1234")
    private String password;
}