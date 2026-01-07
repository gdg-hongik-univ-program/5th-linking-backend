package com.gdg.linking.domain.user.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Schema(description = "로그인 요청 객체")
//무결성을 위한 기본 생성자
@NoArgsConstructor
public class UserLoginRequest {

    @Schema(description = "로그인 아이디", example = "ss1234")
    private String loginId;

    @Schema(description = "비밀번호", example = "password123!")
    private String password;
}
