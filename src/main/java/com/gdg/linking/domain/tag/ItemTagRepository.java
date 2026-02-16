package com.gdg.linking.domain.tag;

import com.gdg.linking.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ItemTagRepository extends JpaRepository<ItemTag, Long> {
    @Modifying
    @Transactional

    // 전달받은 아이템 리스트에 속한 모든 태그 연결 정보를 삭제
    void deleteByItemIn(List<Item> items);
}