package com.gdg.linking.domain.profile;

import com.gdg.linking.domain.profile.dto.ProfileResponse;
import com.gdg.linking.domain.tag.TagService;
import com.gdg.linking.domain.tag.dto.TagStatResponse;
import com.gdg.linking.global.aop.LoginCheck;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final TagService tagService;

    @LoginCheck
    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(HttpSession session) {
        Long userId = getLoginUserId(session);

        ProfileResponse response = profileService.getUserProfile(userId);

        return ResponseEntity.ok(response);
    }

    @LoginCheck
    @GetMapping("/my/stats") // 내 통계 보기
    public ResponseEntity<List<TagStatResponse>> getMyProfileStats(HttpSession session) {
        Long userId = getLoginUserId(session);
        return ResponseEntity.ok(tagService.getTop5TagStats(userId));
    }
}
