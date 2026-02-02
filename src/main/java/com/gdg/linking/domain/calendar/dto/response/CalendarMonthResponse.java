package com.gdg.linking.domain.calendar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarMonthResponse {
    private int year;
    private int month;
    private Map<String, DaySummary> calendarSummary;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DaySummary {
        private int createdCount;
        private int deadlineCount;
    }
}