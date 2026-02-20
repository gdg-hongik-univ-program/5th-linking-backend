package com.gdg.linking.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Schema(description = "회원가입 응답 객체")
public class UserCreateResponse {

    @Schema(description = "생성된 사용자 아이디", example = "ss1234")
    private String loginId;

    @Schema(description = "사용자가 사용할 이미지", example = "PAWN")
    private String image;

}
