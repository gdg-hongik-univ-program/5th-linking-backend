package com.gdg.linking.domain.notification;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    // 아이템 생성 시 호출하여 알림 4개 예약 저장
    void scheduleDeadlineNotifications(Item item);

    // 아이템 수정/삭제 시 호출하여 기존 예약 취소
    void deleteReservedNotifications(Long itemId);

    //void createDeadlineNotifications();

    // 매달 정리 알림
    void createMonthlyCleanupNotifications();

    // 알림 목록 조회
    List<NotificationResponse> getNotifications(Long userId);

    // 알림 읽음 처리
    void markAsRead(Long notificationId, Long userId);
}