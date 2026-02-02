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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createDeadlineNotifications() {
        // 오늘 날짜 계산
        LocalDate today = LocalDate.now();

        // 알림 생성 로직 실행
        checkAndCreate(today.plusDays(7), "7일 남았어요", "D-7");
        checkAndCreate(today.plusDays(3), "3일 남았어요", "D-3");
        checkAndCreate(today.plusDays(1), "하루 남았어요", "D-1");
        checkAndCreate(today, "오늘 마감이에요!", "D-DAY");
    }

    private void checkAndCreate(LocalDate targetDate, String messageTag, String type) {
        // DB에서 특정 날짜가 마감인 아이템들을 한 번에 가져옴
        List<Item> items = itemRepository.findByDeadlineAndStatus(targetDate, Item.ItemStatus.ACTIVE);

        for (Item item : items) {
            Notification notification = Notification.builder()
                    .user(item.getUser())
                    .item(item)
                    .type(type)
                    .message("'" + item.getTitle() + "'의 마감일까지 " + messageTag)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        // 최신순으로 알림 가져오기
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationResponse.builder()
                        .notificationId(n.getId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .isRead(n.isRead())
                        .itemId(n.getItem() != null ? n.getItem().getItemId() : null) // 사용자가 알림을 클릭했을때 해당 Item으로 넘어갈 수 있게 itemId 전달
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("해당 알림을 찾을 수 없습니다."));

        // 알림에 저장된 유저 ID와 현재 로그인한 유저 ID가 일치하는지 대조
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 알림에 대한 접근 권한이 없습니다.");
        }

        // 읽음 상태 변경
        // @Transactional 어노테이션 덕분에 별도의 save() 호출 없이도 메서드가 끝날 때 Dirty Checking에 의해 DB에 자동으로 반영
        notification.setRead(true);
    }

    @Override
    @Transactional
    public void createMonthlyCleanupNotifications() {

        List<User> users = userRepository.findAll();

        for (User user : users) {
            // 정리 권유 알림 객체 생성
            Notification notification = Notification.builder()
                    .user(user)
                    .type("CLEANUP") // 알림 유형 구분
                    .message("벌써 한 달의 절반이 지났어요! 저장해둔 링크들을 정리하며 생각을 비워볼까요? 🧹")
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
        }
    }
}
