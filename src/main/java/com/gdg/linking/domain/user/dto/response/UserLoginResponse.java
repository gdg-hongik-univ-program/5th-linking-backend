package com.gdg.linking.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginResponse {

    private String loginId;

    public UserLoginResponse(String loginId) {
        this.loginId = loginId;
    }
}
