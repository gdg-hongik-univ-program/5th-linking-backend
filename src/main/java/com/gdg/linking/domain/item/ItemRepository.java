package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>{

    List<Item> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT i FROM Item i JOIN i.relatedItems ri WHERE ri.itemId = :itemId")
    List<Item> findItemsLinkingToMe(@Param("itemId") Long itemId);

    List<Item> findByFolder_fId(Long fId);

    List<Item> findByDeadlineAndStatus(LocalDate deadline, Item.ItemStatus status);
    // 특정 사용자의 아이템 중, deadline이 특정 기간 사이인 데이터 조회 (마감일순 정렬)
    List<Item> findByUser_UserIdAndDeadlineBetweenOrderByDeadlineAsc(
            Long userId, LocalDate start, LocalDate end);

    // 특정 사용자의 아이템 중, 생성일이 특정 기간 사이인 데이터 조회
    List<Item> findByUser_UserIdAndCreatedAtBetween(
            Long userId, LocalDateTime start, LocalDateTime end);
}
