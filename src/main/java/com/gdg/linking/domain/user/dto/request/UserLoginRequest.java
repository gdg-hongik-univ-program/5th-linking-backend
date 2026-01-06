package com.gdg.linking.domain.user.dto.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginRequest {

    private String loginId;
    private String password;
}
