package com.gdg.linking.domain.calendar;


import com.gdg.linking.domain.calendar.dto.request.CalendarPageRequest;
import com.gdg.linking.domain.calendar.dto.response.CalendarMonthResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarPageResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarSummaryDto;
import com.gdg.linking.domain.calendar.dto.response.EventDetailDto;
import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class CalendarServiceImpl implements CalendarService {

    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public CalendarPageResponse getCalendarPageData(CalendarPageRequest request, Long userId) {

        // 1. 해당 월의 시작일과 종료일 계산
        LocalDate startLocalDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate endLocalDate = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth());

        // LocalDateTime 범위 (생성일 조회용)
        LocalDateTime startDateTime = startLocalDate.atStartOfDay();
        LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

        // 2. DB에서 데이터 조회
        // 마감일(Deadline) 기준 데이터 (빨간 점 및 리스트용)
        List<Item> deadlineItems = itemRepository.findByUser_UserIdAndDeadlineBetweenOrderByDeadlineAsc(
                userId, startLocalDate, endLocalDate);

        // 생성일(CreatedAt) 기준 데이터 (노란 점용)
        List<Item> createdItems = itemRepository.findByUser_UserIdAndCreatedAtBetween(
                userId, startDateTime, endDateTime);

        // 3. DTO 변환 (기존에 만든 정적 메서드 활용)
        List<CalendarSummaryDto> summary = Stream.concat(
                deadlineItems.stream().map(CalendarSummaryDto::from),
                createdItems.stream().map(CalendarSummaryDto::from)
        ).distinct().collect(Collectors.toList()); // 중복 제거

        List<EventDetailDto> eventList = deadlineItems.stream()
                .map(EventDetailDto::from)
                .collect(Collectors.toList());

        // 4. 최종 응답 반환
        return CalendarPageResponse.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .calendarSummary(summary)
                .eventList(eventList)
                .build();
    }

    @Transactional
    @Override
    public CalendarMonthResponse getCalendarMonthData(CalendarPageRequest request, Long userId) {

        LocalDate startLocalDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate endLocalDate = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth());


        // LocalDateTime 범위 (생성일 조회용)
        LocalDateTime startDateTime = startLocalDate.atStartOfDay();
        LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

        // 2. DB에서 데이터 조회
        // 마감일(Deadline) 기준 데이터 (빨간 점 및 리스트용)
        List<Item> deadlineItems = itemRepository.findByUser_UserIdAndDeadlineBetweenOrderByDeadlineAsc(
                userId, startLocalDate, endLocalDate);

        // 생성일(CreatedAt) 기준 데이터 (노란 점용)
        List<Item> createdItems = itemRepository.findByUser_UserIdAndCreatedAtBetween(
                userId, startDateTime, endDateTime);


        // 3. DTO 변환 (기존에 만든 정적 메서드 활용)
        List<CalendarSummaryDto> summary = Stream.concat(
                deadlineItems.stream().map(CalendarSummaryDto::from),
                createdItems.stream().map(CalendarSummaryDto::from)
        ).distinct().collect(Collectors.toList()); // 중복 제거

        return CalendarMonthResponse.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .calendarSummary(summary)
                .build();
    }

}
