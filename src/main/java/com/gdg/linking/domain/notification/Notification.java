package com.gdg.linking.domain.notification;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // 알림을 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 마감일 알림일 경우 어떤 item인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false, length = 45)
    private String type; // DEADLINE, CLEANUP 등

    @Column(name = "message_data", nullable = false, length = 255)
    private String message;

    // 알림 확인 여부
    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @CreatedDate // 알림이 생성될 때 시간을 자동으로 저장
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;
}
