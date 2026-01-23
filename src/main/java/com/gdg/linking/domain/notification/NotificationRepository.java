package com.gdg.linking.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 유저의 알림을 최신순(내림차순)으로 조회
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 읽지 않은 알림이 있는지 여부 확인 (빨간 점)
    boolean existsByUser_UserIdAndIsReadFalse(Long userId);
}