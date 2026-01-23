package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>{

    List<Item> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 이 부분을 추가해야 합니다!
    @Query("SELECT i FROM Item i JOIN i.relatedItems ri WHERE ri.itemId = :itemId")
    List<Item> findItemsLinkingToMe(@Param("itemId") Long itemId);

    List<Item> findByFolder_fId(Long fId);

    List<Item> findByDeadlineAndStatus(LocalDate deadline, Item.ItemStatus status);
}
