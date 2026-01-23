package com.gdg.linking.domain.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // 매일 아침 8시에 실행되도록 설정
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void runDeadlineCheck() {
        log.info("마감일 알림 체크 스케줄러 시작 - 아침 8시");
        notificationService.createDeadlineNotifications();
        log.info("마감일 알림 체크 스케줄러 완료");
    }

    @Scheduled(cron = "0 0 8 15 * *", zone = "Asia/Seoul")
    public void runMonthlyCleanupCheck() {
        log.info("매달 15일 정리 권유 알림 생성 시작");
        notificationService.createMonthlyCleanupNotifications();
    }
}
