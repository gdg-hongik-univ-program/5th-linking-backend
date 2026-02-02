package com.gdg.linking.domain.calendar.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarPageResponse {
    private int year;
    private int month;
    // 상단 캘린더 점(Dot) 표시를 위한 요약 리스트
    private List<CalendarSummaryDto> calendarSummary;

    // 하단 상세 정보를 보여주기 위한 이벤트 리스트
    private List<EventDetailDto> eventList;


}