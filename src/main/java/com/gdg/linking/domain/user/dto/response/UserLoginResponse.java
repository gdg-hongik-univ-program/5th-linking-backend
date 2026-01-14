package com.gdg.linking.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 객체")
public class UserLoginResponse {

    private Long userId;

    @Schema(description = "로그인 아이디", example = "ss1234")
    private String loginId;

    @Schema(description = "관리자 여부", example = "false")
    private boolean isAdmin;


}
