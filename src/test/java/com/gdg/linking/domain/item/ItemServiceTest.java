package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("아이템 생성 성공 테스트")
    void createItemTest() {
        // 1. Given (준비)
        // 주의: 로컬 DB에 ID가 1인 유저가 실제로 있어야 합니다.
        Long userId = 1L;

        // ItemCreateRequest 생성 (생성자나 @Setter가 있어야 함)
        // Reflection 등을 쓰거나 생성자를 통해 값을 채웁니다.
        ItemCreateRequest request = new ItemCreateRequest();
        // ... request 필드 채우기 (생성자가 없다면 리플렉션이나 빌더 활용)

        // 2. When (실행)
        ItemCreateResponse response = itemService.createItem(request, userId);

        // 3. Then (검증)
        assertThat(response).isNotNull();
        assertThat(response.getItemId()).isNotNull();
        System.out.println("생성된 아이템 ID: " + response.getItemId());
    }


}
