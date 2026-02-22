package com.gdg.linking.domain.calendar;


import com.gdg.linking.domain.calendar.dto.response.CalendarMonthResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarDayResponse;
import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class CalendarServiceImpl implements CalendarService {

    private final ItemRepository itemRepository;
    /**
     * 월별 캘린더 요약 정보 조회
     */

    @Override
    @Transactional
    public CalendarMonthResponse getCalendarMonthData(int year, int month, Long userId) {
        // 1. 해당 월의 범위 계산
        LocalDate startLocalDate = LocalDate.of(year, month, 1);
        LocalDate endLocalDate = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth());
        LocalDateTime startDateTime = startLocalDate.atStartOfDay();
        LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

        // 2. DB에서 ACTIVE 상태인 데이터만 조회
        // 마감일 기준 ACTIVE 아이템 조회
        List<Item> deadlineItems = itemRepository.findByUser_UserIdAndDeadlineBetweenAndStatusOrderByDeadlineAsc(
                userId, startLocalDate, endLocalDate, Item.ItemStatus.ACTIVE);

        // 생성일 기준 ACTIVE 아이템 조회
        List<Item> createdItems = itemRepository.findByUser_UserIdAndCreatedAtBetweenAndStatus(
                userId, startDateTime, endDateTime, Item.ItemStatus.ACTIVE);

        // 3. 날짜별로 데이터 집계
        Map<String, CalendarMonthResponse.DaySummary> summaryMap = new TreeMap<>();

        for (Item item : createdItems) {
            String dateKey = item.getCreatedAt().toLocalDate().toString();
            CalendarMonthResponse.DaySummary current = summaryMap.getOrDefault(dateKey, new CalendarMonthResponse.DaySummary(0, 0));
            summaryMap.put(dateKey, new CalendarMonthResponse.DaySummary(current.getCreatedCount() + 1, current.getDeadlineCount()));
        }

        for (Item item : deadlineItems) {
            String dateKey = item.getDeadline().toString();
            CalendarMonthResponse.DaySummary current = summaryMap.getOrDefault(dateKey, new CalendarMonthResponse.DaySummary(0, 0));
            summaryMap.put(dateKey, new CalendarMonthResponse.DaySummary(current.getCreatedCount(), current.getDeadlineCount() + 1));
        }

        return new CalendarMonthResponse(year, month, summaryMap);
    }

    @Override
    @Transactional
    public CalendarDayResponse getCalendarDayData(LocalDate date, Long userId) {
        // 1. 해당 날짜가 마감일이면서 ACTIVE인 '내 아이템' 조회 (DB 레벨 필터링)
        List<Item> deadlineItems = itemRepository.findByUser_UserIdAndDeadlineAndStatus(
                userId, date, Item.ItemStatus.ACTIVE);

        // 2. 해당 날짜가 생성일이면서 ACTIVE인 '내 아이템' 조회
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
        List<Item> createdItems = itemRepository.findByUser_UserIdAndCreatedAtBetweenAndStatus(
                userId, startDateTime, endDateTime, Item.ItemStatus.ACTIVE);

        // 3. 데이터 병합 (중복 제거) 및 DTO 변환
        List<CalendarDayResponse.EventDetailDto> eventList = Stream.concat(deadlineItems.stream(), createdItems.stream())
                .distinct()
                .map(item -> CalendarDayResponse.EventDetailDto.builder()
                        .itemId(item.getItemId())
                        .title(item.getTitle())
                        .memo(item.getMemo())
                        .deadline(item.getDeadline())
                        .createdAt(item.getCreatedAt())
                        .tag(item.getItemTags() != null ?
                                item.getItemTags().stream()
                                        .map(itemTag -> itemTag.getTag().getTagName())
                                        .collect(Collectors.toList())
                                : Collections.emptyList())
                        .importance(item.isImportance())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return CalendarDayResponse.builder()
                .selectedDate(date)
                .eventList(eventList)
                .build();
    }
}
