package com.gdg.linking.domain.user;


import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.gdg.linking.global.utils.SessionUtil.setLoginAdminId;
import static com.gdg.linking.global.utils.SessionUtil.setLoginUserId;

//swaager 관련 태그
@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("user")

public class UserController {


    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }


    @PostMapping("sign-up")
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 아이디, 비밀번호, 이메일, 닉네임이 필요합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디")
    })
    public ResponseEntity<UserCreateResponse> signUp(@RequestBody UserCreateRequest request){

        UserCreateResponse user = userService.register(request);

        return ResponseEntity.created(URI.create("/user"+ user))
                .body(user);
    }


    @PostMapping("sign-in")
    @Operation(
            summary = "로그인",
            description = "사용자 로그인을 진행합니다. 아이디, 비밀번호가 일치해야 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "잘못된 요청 파라미터")
    })
    public ResponseEntity<UserLoginResponse> signIn(@RequestBody UserLoginRequest request,
                                                    HttpSession session){



        UserLoginResponse result = userService.login(request);
        if(result.isAdmin()){

            //어드민 세션 추가
            setLoginAdminId(session, request.getLoginId());

        } else{

            //유저 세션 추가
            setLoginUserId(session,request.getLoginId());

        }

        return ResponseEntity.ok(result);

    }






}
