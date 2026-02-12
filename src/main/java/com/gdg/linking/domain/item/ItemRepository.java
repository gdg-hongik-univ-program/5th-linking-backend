package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>{

    // 필터가 없을 때 ACTIVE 상태인 것만 최신순으로 조회
    //N+1문제 해결을 위한 코드
    @Query("select distinct i from Item i " +
            "left join fetch i.folder " +
            "left join fetch i.itemTags it " +
            "left join fetch it.tag " +
            "where i.user.userId = :userId and i.status = :status " +
            "order by i.createdAt desc")
    List<Item> findByUser_UserIdAndStatusOrderByCreatedAtDesc(Long userId, Item.ItemStatus status);

    // 마감 임박 (오늘 ~ 7일 뒤, ACTIVE 상태만)
    List<Item> findByUser_UserIdAndDeadlineBetweenAndStatusOrderByDeadlineAsc(
            Long userId, LocalDate start, LocalDate end, Item.ItemStatus status);

    // 중요 표시이면서 ACTIVE 상태인 것만 조회
    List<Item> findByUser_UserIdAndImportanceTrueAndStatus(Long userId, Item.ItemStatus status);

    // 청소 Item 조회 기능에서 수정된 적이 있다면 수정일 기준, 없다면 생성일 기준으로 Item을 조회
    @Query("SELECT i FROM Item i " +
            "WHERE i.user.userId = :userId " +
            "AND i.status = :status " +
            "AND (COALESCE(i.updatedAt, i.createdAt) < :targetDate)")
    List<Item> findStaleItems(
            @Param("userId") Long userId,
            @Param("targetDate") LocalDateTime targetDate,
            @Param("status") Item.ItemStatus status);

    // 휴지통 목록 조회
    List<Item> findByUser_UserIdAndStatusOrderByDeletedAtDesc(Long userId, Item.ItemStatus status);

    // 상태가 TRASH이고 deletedAt이 30일 이전인 아이템 조회
    List<Item> findByStatusAndDeletedAtBefore(Item.ItemStatus status, LocalDate threshold);

    // UserId와 Status 조회
    List<Item> findByUser_UserIdAndStatus(Long userId, Item.ItemStatus status);

    // 최신순으로 상위 8개만 조회 (ACTIVE 상태인 아이템만)
    List<Item> findTop8ByUser_UserIdAndStatusOrderByCreatedAtDesc(Long userId, Item.ItemStatus status);

    @Query("SELECT i FROM Item i JOIN i.relatedItems ri WHERE ri.itemId = :itemId")
    List<Item> findItemsLinkingToMe(@Param("itemId") Long itemId);

    List<Item> findByFolder_fIdAndStatus(Long fId,Item.ItemStatus status);

    List<Item> findByDeadlineAndStatus(LocalDate deadline, Item.ItemStatus status);

    // 특정 사용자의 아이템 중, deadline이 특정 기간 사이인 데이터 조회 (마감일순 정렬)
    List<Item> findByUser_UserIdAndDeadlineBetweenOrderByDeadlineAsc(
            Long userId, LocalDate start, LocalDate end);

    // 특정 사용자의 아이템 중, 생성일이 특정 기간 사이인 데이터 조회
    @EntityGraph(attributePaths = {"itemTags"})
    List<Item> findByUser_UserIdAndCreatedAtBetween(
            Long userId, LocalDateTime start, LocalDateTime end);

    // 사용자의 전체 Item 개수
    long countByUser_UserIdAndStatus(Long userId, Item.ItemStatus status);


    @Query("SELECT DISTINCT i FROM Item i " +
            "LEFT JOIN i.folder f " +
            "LEFT JOIN i.itemTags it " +
            "LEFT JOIN it.tag t " +
            "WHERE i.user.userId = :userId " +
            "AND (i.title LIKE %:keyword% " +
            "OR t.tagName LIKE %:keyword%)")
    Slice<Item> searchItemsByKeyword(@Param("userId") Long userId,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);


    @Query("SELECT DISTINCT i FROM Item i " +
            "JOIN i.itemTags it " + // 아이템과 태그의 연결고리(ItemTag) 조인
            "JOIN it.tag t " +      // 실제 태그 엔티티 조인
            "WHERE i.user.userId = :userId " +
            "AND t.tagName LIKE %:keyword%") // 태그 이름에 키워드가 포함된 경우
    Slice<Item> findItemsByTagName(@Param("userId") Long userId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);
}
