package com.gdg.linking.domain.profile;

import com.gdg.linking.domain.profile.dto.ProfileGraphResponse;
import com.gdg.linking.domain.profile.dto.ProfileResponse;
import com.gdg.linking.domain.tag.TagService;
import com.gdg.linking.domain.tag.dto.TagStatResponse;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "내 프로필 조회",
            description = "사용자의 프로필을 조회합니다.")
    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(HttpSession session) {
        Long userId = getLoginUserId(session);

        ProfileResponse response = profileService.getUserProfile(userId);

        return ResponseEntity.ok(response);
    }

    @LoginCheck
    @Operation(summary = "사용자 상위 태그 5개 조회",
            description = "사용자의 상위 태그 5개를 조회합니다.")
    @GetMapping("/my/stats") // 내 통계 보기
    public ResponseEntity<List<TagStatResponse>> getMyProfileStats(HttpSession session) {
        Long userId = getLoginUserId(session);
        return ResponseEntity.ok(tagService.getTop5TagStats(userId));
    }

    // 옵시디언 뷰
    @LoginCheck
    @Operation(summary = "프로필 옵시디언 뷰 데이터 조회",
            description = "내 프로필 페이지에서 보여줄 지식 그래프(아이템 관계) 데이터를 조회합니다. (importance가 1이면 중요, 0이면 일반 Item)")
    @GetMapping("/graph")
    public ResponseEntity<ProfileGraphResponse> getMyGraph(HttpSession session) {
        Long userId = getLoginUserId(session); // 세션에서 유저 ID 추출
        ProfileGraphResponse response = profileService.getProfileGraph(userId);
        return ResponseEntity.ok(response);
    }
}
