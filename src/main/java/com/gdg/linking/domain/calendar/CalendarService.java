package com.gdg.linking.domain.calendar;

import com.gdg.linking.domain.calendar.dto.request.CalendarPageRequest;
import com.gdg.linking.domain.calendar.dto.response.CalendarMonthResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarDayResponse;

import java.time.LocalDate;

public interface CalendarService {

    CalendarMonthResponse getCalendarMonthData(int year, int month, Long userId);

    CalendarDayResponse getCalendarDayData(LocalDate date, Long userId);

}
