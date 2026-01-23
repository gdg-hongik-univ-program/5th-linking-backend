package com.gdg.linking.domain.notification;

import com.gdg.linking.domain.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    void createDeadlineNotifications();

    // 매달 정리 알림
    void createMonthlyCleanupNotifications();

    // 알림 목록 조회
    List<NotificationResponse> getNotifications(Long userId);

    // 알림 읽음 처리
    void markAsRead(Long notificationId);
}