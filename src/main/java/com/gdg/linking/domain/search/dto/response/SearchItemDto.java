package com.gdg.linking.domain.search.dto.response;

import com.gdg.linking.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SearchItemDto {
    private Long ItemId;
    private String title;
    private String url;
    // ... 필요한 필드들

    // 정적 팩토리 메서드 (SearchResponse에서 사용 중인 부분)
    public static SearchItemDto from(Item item) {
        return new SearchItemDto(
                item.getItemId(),
                item.getTitle(),
                item.getUrl()
        );
    }
}