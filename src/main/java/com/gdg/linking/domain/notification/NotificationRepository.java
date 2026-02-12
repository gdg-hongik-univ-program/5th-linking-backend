package com.gdg.linking.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.user.userId = :userId AND n.scheduledDate <= :now")
    void deleteVisibleNotificationsByUserId(@Param("userId") Long userId, @Param("now") LocalDate now);

    // 특정 유저에게 노출되었고 읽지 않은 알림을 읽음 처리
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true " +
            "WHERE n.user.userId = :userId " +
            "AND n.isRead = false " +
            "AND n.scheduledDate <= :now")
    void markVisibleNotificationsAsRead(@Param("userId") Long userId, @Param("now") LocalDate now);

}