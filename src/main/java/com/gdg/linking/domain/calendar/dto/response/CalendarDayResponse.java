package com.gdg.linking.domain.calendar.dto.response;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.tag.ItemTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayResponse {
    private LocalDate selectedDate;
    // 하단 상세 정보를 보여주기 위한 이벤트 리스트
    private List<EventDetailDto> eventList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDetailDto {
        private Long itemId;
        private String title;
        private String memo;

        // 이미지에 있는 #패션 등의 태그 (현재 엔티티에 미구현이므로 폴더명 등으로 대체 가능)
        private List<String> tag;

        // 프론트엔드에서 D-1 등을 계산하기 위한 원본 날짜
        private LocalDate deadline;
        private LocalDateTime createdAt;
        // 별점 표시 (이미지의 별 UI 대응용, 엔티티에 중요도 importance가 있으므로 이를 활용)
        private boolean importance;

        @Schema(description = "썸네일 저장 링크", example = "[\"요리\", \"레시피\"]")
        private String imageUrl;

    }
}