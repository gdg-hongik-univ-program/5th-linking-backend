package com.gdg.linking.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Schema(description = "회원가입 요청 객체")
@NoArgsConstructor
public class UserCreateRequest {

    @Schema(description = "로그인 아이디", example = "ss1234")
    private String loginId;

    @Schema(description = "비밀번호", example = "password123!")
    private String password;

    @Schema(description = "이메일", example = "user@naver.com")
    private String email;

    @Schema(description = "닉네임", example = "김생산")
    private String nickName;

}
