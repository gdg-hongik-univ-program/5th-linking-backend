package com.gdg.linking.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateResponse {

    private String loginId;

    public UserCreateResponse(String loginId) {
        this.loginId = loginId;
    }
}
