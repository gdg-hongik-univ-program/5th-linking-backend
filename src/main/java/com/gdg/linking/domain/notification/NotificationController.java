package com.gdg.linking.domain.notification;

import com.gdg.linking.domain.notification.dto.NotificationResponse;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "알림 관련 API")
@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @LoginCheck
    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "로그인한 유저의 알림을 최신순으로 가져옵니다.")
    public ResponseEntity<List<NotificationResponse>> getNotifications(HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @PatchMapping("/{notification_id}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<Void> markAsRead(@PathVariable("notification_id") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
