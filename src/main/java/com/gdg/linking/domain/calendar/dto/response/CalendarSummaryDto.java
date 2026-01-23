package com.gdg.linking.domain.calendar.dto.response;

import com.gdg.linking.domain.item.Item;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CalendarSummaryDto {
    private Long itemId;
    private String title;

    // 노란색 점(생성일) 표시용
    private LocalDateTime createdAtDate;

    // 빨간색 점(데드라인) 표시용
    private LocalDate deadline;

    // 엔티티로부터 DTO를 생성하는 정적 메서드
    public static CalendarSummaryDto from(Item item) {
        return CalendarSummaryDto.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .createdAtDate(item.getCreatedAt())
                .deadline(item.getDeadline())
                .build();
    }
}