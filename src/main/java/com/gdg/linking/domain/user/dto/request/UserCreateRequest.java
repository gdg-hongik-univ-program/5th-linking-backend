package com.gdg.linking.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String loginId;
    private String password;
    private String email;
    private String nickName;

}
