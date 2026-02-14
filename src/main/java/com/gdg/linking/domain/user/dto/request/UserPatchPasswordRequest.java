package com.gdg.linking.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Schema(description = "비밀번호 번경 요청")
@NoArgsConstructor
public class UserPatchPasswordRequest {

    @Schema(description = "변경할 비밀번호", example = "newpassword123!")
    private String password;

}
