package com.gdg.linking.domain.user;

import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.request.UserPatchNickRequest;
import com.gdg.linking.domain.user.dto.request.UserPatchPasswordRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserInfoResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;

public interface UserService {


    UserCreateResponse register(UserCreateRequest request);

    UserLoginResponse login(UserLoginRequest request);

    Boolean findById(String id);

    void patchNickName(Long userId, UserPatchNickRequest request);

    void patchPassword(Long userId, UserPatchPasswordRequest request);

    Boolean checkPassword(Long userId, String password);

    UserInfoResponse getUserInfo(Long userId);
}
