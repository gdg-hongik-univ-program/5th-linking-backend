package com.gdg.linking.domain.notification;


import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import com.gdg.linking.domain.notification.dto.NotificationResponse;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    // private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public void scheduleDeadlineNotifications(Item item) {
        LocalDate deadline = item.getDeadline();
        if (deadline == null) return;

        // D-7, D-3, D-1, D-DAY 알림 예약
        saveScheduled(item, deadline.minusDays(7), "7일 남았어요", "D-7");
        saveScheduled(item, deadline.minusDays(3), "3일 남았어요", "D-3");
        saveScheduled(item, deadline.minusDays(1), "하루 남았어요", "D-1");
        saveScheduled(item, deadline, "오늘이에요!", "D-DAY");
    }

    private void saveScheduled(Item item, LocalDate scheduledDate, String messageTag, String type) {
        // 이미 날짜가 지난 알림은 생성하지 않음
        if (!scheduledDate.isAfter(LocalDate.now())) return;

        Notification notification = Notification.builder()
                .user(item.getUser())
                .item(item)
                .type(type)
                .message("'" + item.getTitle() + "' 마감일이 " + messageTag)
                .scheduledDate(scheduledDate)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteReservedNotifications(Long itemId) {
        notificationRepository.deleteByItem_ItemIdAndIsReadFalse(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        // [수정] 오늘 날짜 기준으로 예약된 알림만 꺼내옴
        return notificationRepository.findByUser_UserIdAndScheduledDateLessThanEqualOrderByCreatedAtDesc(userId, LocalDate.now())
                .stream()
                .map(n -> NotificationResponse.builder()
                        .notificationId(n.getId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .isRead(n.isRead())
                        .itemId(n.getItem() != null ? n.getItem().getItemId() : null)
                        .createdAt(n.getCreatedAt())
                        .scheduledDate(n.getScheduledDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("해당 알림을 찾을 수 없습니다."));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 알림에 대한 접근 권한이 없습니다.");
        }

        // 읽음 상태 변경
        // @Transactional 어노테이션 덕분에 별도의 save() 호출 없이도 메서드가 끝날 때 Dirty Checking에 의해 DB에 자동으로 반영됩니다.
        notification.setRead(true);
    }

    // 해당 유저의 모든 알림 데이터를 삭제
    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        // 해당 유저에게 노출된 알림 데이터를 삭제
        notificationRepository.deleteVisibleNotificationsByUserId(userId, LocalDate.now());
    }


    // 벌크 업데이트를 통해 한 번의 쿼리로 모든 알림을 읽음 처리
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {

        notificationRepository.markVisibleNotificationsAsRead(userId, LocalDate.now());

    }

    @Override
    @Transactional
    public void createMonthlyCleanupNotifications() {
        // 모든 사용자 조회
        List<User> users = userRepository.findAll();

        // 50일 전 시점 계산
        LocalDateTime threshold = LocalDateTime.now().minusDays(50);

        for (User user : users) {
            // 방치된 아이템 개수 쿼리 실행
            long staleCount = itemRepository.countByUser_UserIdAndStatusAndCreatedAtBefore(
                    user.getUserId(),
                    Item.ItemStatus.ACTIVE,
                    threshold
            );

            // 방치된 아이템이 1개라도 있을 때만 알림 생성
            if (staleCount > 0) {
                Notification notification = Notification.builder()
                        .user(user)
                        .type("CLEANUP")
                        .message(String.format("벌써 한 달의 절반이 지났어요! 방치된 %d개의 링크를 정리하며 생각을 비워볼까요? 🧹", staleCount))
                        .isRead(false)
                        .scheduledDate(LocalDate.now())
                        .build();

                notificationRepository.save(notification);
            }
        }
    }
}
