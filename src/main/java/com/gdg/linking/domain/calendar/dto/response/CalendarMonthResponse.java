package com.gdg.linking.domain.calendar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarMonthResponse {

    private int year;
    private int month;
    // 상단 캘린더 점(Dot) 표시를 위한 요약 리스트
    private List<CalendarSummaryDto> calendarSummary;

}
