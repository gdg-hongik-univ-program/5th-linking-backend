package com.gdg.linking.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

//무결성을 위한 기본 생성자
@Getter
@Schema(description = "닉네임 변경 요청")
@NoArgsConstructor
public class UserPatchNickRequest {

    @Schema(description = "변경할 닉네임", example = "김생산")
    private String nickName;

    @Schema(description = "변경할 코드", example = "PAWN")
    private String imageCode

}

