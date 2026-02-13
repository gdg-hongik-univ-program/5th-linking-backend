package com.gdg.linking.domain.user;


import com.gdg.linking.domain.user.dto.request.*;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserInfoResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.gdg.linking.global.utils.SessionUtil.*;

//swaager 관련 태그
@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("user")

public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/dup")
    @Operation(
            summary = "아이디 중복 확인로직",
            description = "기존 아이디 값과 중복되는지 확인합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디")
    })
    public ResponseEntity<Boolean> dup(@RequestBody UserCreateRequest request){


        Boolean exist = userService.findById(request.getLoginId());

        return ResponseEntity.ok(exist);
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

        session.setAttribute("LOGIN_USER_ID", result.getUserId());

        return ResponseEntity.ok(result);

    }

    // 설정창 진입 전 비밀번호 확인
    @LoginCheck
    @PostMapping("/check-password")
    @Operation(summary = "비밀번호 확인", description = "마이페이지 접근 전 비밀번호를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 일치"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치")
    })
    public ResponseEntity<Boolean> checkPassword(
            @RequestBody UserCheckPasswordRequest request,
            HttpSession session
    ) {
        Long userId = getLoginUserId(session);
        // 일치하면 true, 아니면 예외 발생 혹은 false 리턴
        Boolean isMatch = userService.checkPassword(userId, request.getPassword());

        return ResponseEntity.ok(isMatch);
    }

    // 설정창에 보여줄 내 정보 조회
    @LoginCheck
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "설정창에 표시할 사용자 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> getUserInfo(HttpSession session) {
        Long userId = getLoginUserId(session);
        UserInfoResponse userInfo = userService.getUserInfo(userId);

        return ResponseEntity.ok(userInfo);
    }


    @LoginCheck
    @PatchMapping("nickname")
    @Operation(
            summary = "닉네임 변경",
            description = "사용자 닉네임을 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @ApiResponse(responseCode = "401", description = "잘못된 요청 파라미터")
    })
    public ResponseEntity<Void> patchNickName(
            @RequestBody UserPatchNickRequest request,
            HttpSession session
            ) {

        Long userId = getLoginUserId(session);
        userService.patchNickName(userId, request);

        return ResponseEntity.ok().build();
    }



    @LoginCheck
    @PatchMapping("password")
    @Operation(
            summary = "비밀번호 번경",
            description = "사용자 비밀번호를 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @ApiResponse(responseCode = "401", description = "잘못된 요청 파라미터")
    })
    public ResponseEntity<Void> patchPassword(
            @RequestBody UserPatchPasswordRequest request,
            HttpSession session
    ) {

        Long userId = getLoginUserId(session);
        userService.patchPassword(userId, request);

        return ResponseEntity.ok().build();
    }


}
