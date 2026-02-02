package com.gdg.linking.domain.calendar;

import com.gdg.linking.domain.calendar.dto.request.CalendarPageRequest;
import com.gdg.linking.domain.calendar.dto.response.CalendarMonthResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarPageResponse;

public interface CalendarService {
    CalendarPageResponse getCalendarPageData(CalendarPageRequest request, Long userId);

    CalendarMonthResponse getCalendarMonthData(CalendarPageRequest request, Long userId);
}
