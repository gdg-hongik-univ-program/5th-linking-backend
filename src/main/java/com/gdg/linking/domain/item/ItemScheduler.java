package com.gdg.linking.domain.item;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemScheduler {
    private final ItemRepository itemRepository;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시 실행
    @Transactional
    public void cleanOldTrash() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        // 상태가 TRASH이고 deletedAt이 30일 이전인 아이템 조회
        List<Item> oldItems = itemRepository.findByStatusAndDeletedAtBefore(Item.ItemStatus.TRASH, threshold);
        itemRepository.deleteAllInBatch(oldItems);
    }
}
