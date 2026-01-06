package com.gdg.linking.domain.user;


import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
@RequestMapping("user")
public class UserController {


    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }


    @PostMapping("sign-up")
    public ResponseEntity<UserCreateResponse> signUp(@RequestBody UserCreateRequest request){

        UserCreateResponse user = userService.register(request);

        return ResponseEntity.created(URI.create("/user/"+ user))
                .body(user);
    }


    @PostMapping("sign-in")
    public ResponseEntity<UserLoginResponse> signIn(@RequestBody UserLoginRequest request){

        UserLoginResponse result = userService.login(request);

        return ResponseEntity.ok(result);

    }





}
