package com.gdg.linking.domain.calendar.dto.response;

import com.gdg.linking.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailDto {
    private Long itemId;
    private String title;
    private String memo;

    // 이미지에 있는 #패션 등의 태그 (현재 엔티티에 미구현이므로 폴더명 등으로 대체 가능)
    private String tag;

    // 프론트엔드에서 D-1 등을 계산하기 위한 원본 날짜
    private LocalDate deadline;

    // 별점 표시 (이미지의 별 UI 대응용, 엔티티에 중요도 importance가 있으므로 이를 활용)
    private boolean importance;

    /**
     * Item 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static EventDetailDto from(Item item) {
        return EventDetailDto.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .memo(item.getMemo())
                .tag(item.getFolder() != null ? item.getFolder().getFolderName() : "기본") // 폴더명을 태그로 우선 활용
                .deadline(item.getDeadline())
                .importance(item.isImportance())
                .build();
    }
}