package com.gdg.linking.domain.user;

import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;

public interface UserService {


    UserCreateResponse register(UserCreateRequest request);

    UserLoginResponse login(UserLoginRequest request);


}
