package com.gdg.linking.domain.profile;

import com.gdg.linking.domain.profile.dto.ProfileResponse;
import com.gdg.linking.global.aop.LoginCheck;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @LoginCheck
    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(HttpSession session) {
        Long userId = getLoginUserId(session);

        ProfileResponse response = profileService.getUserProfile(userId);

        return ResponseEntity.ok(response);
    }
}
