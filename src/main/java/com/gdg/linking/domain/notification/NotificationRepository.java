package com.gdg.linking.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 예약 날짜가 오늘 이하인(오늘이 되었거나 이미 지난) 알림만 조회
    List<Notification> findByUser_UserIdAndScheduledDateLessThanEqualOrderByCreatedAtDesc(Long userId, LocalDate date);

    // 아이템 수정/삭제 시 아직 안 본 예약 알림들 삭제
    @Modifying // 데이터를 변경(삭제)할 때 필수
    @Transactional // 삭제 작업은 트랜잭션 안에서 일어나야 함
    void deleteByItem_ItemIdAndIsReadFalse(Long itemId);

    // 특정 유저의 알림을 최신순(내림차순)으로 조회
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 읽지 않은 알림이 있는지 여부 확인 (빨간 점)
    boolean existsByUser_UserIdAndIsReadFalse(Long userId);
}