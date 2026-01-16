package com.gdg.linking.domain.item.dto.response;

import com.gdg.linking.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RelatedItemResponse {
    private Long itemId;
    private String title;

    // 엔티티에서 필요한 정보만 추출하는 정적 메서드
    public static RelatedItemResponse fromEntity(Item item) {
        return RelatedItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .build();
    }
}