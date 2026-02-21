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

        // 2. DB에서 데이터 조회
        List<Item> deadlineItems = itemRepository.findByUser_UserIdAndDeadlineBetweenOrderByDeadlineAsc(
                userId, startLocalDate, endLocalDate);
        List<Item> createdItems = itemRepository.findByUser_UserIdAndCreatedAtBetween(
                userId, startDateTime, endDateTime);

        // 3. 날짜별로 데이터 집계 (TreeMap을 사용하여 날짜순 정렬)
        Map<String, CalendarMonthResponse.DaySummary> summaryMap = new TreeMap<>();

        // 생성일 기준 카운팅
        for (Item item : createdItems) {
            String dateKey = item.getCreatedAt().toLocalDate().toString();
            CalendarMonthResponse.DaySummary current = summaryMap.getOrDefault(dateKey, new CalendarMonthResponse.DaySummary(0, 0));
            summaryMap.put(dateKey, new CalendarMonthResponse.DaySummary(current.getCreatedCount() + 1, current.getDeadlineCount()));
        }

        // 마감일 기준 카운팅
        for (Item item : deadlineItems) {
            String dateKey = item.getDeadline().toString();
            CalendarMonthResponse.DaySummary current = summaryMap.getOrDefault(dateKey, new CalendarMonthResponse.DaySummary(0, 0));
            summaryMap.put(dateKey, new CalendarMonthResponse.DaySummary(current.getCreatedCount(), current.getDeadlineCount() + 1));
        }

        return new CalendarMonthResponse(year, month, summaryMap);
    }
    /**
     * 특정 날짜의 상세 일정 조회
     */

    @Override
    @Transactional
    public CalendarDayResponse getCalendarDayData(LocalDate date, Long userId) {
        // 1. 해당 날짜가 마감일(Deadline)인 아이템 조회
        List<Item> deadlineItems = itemRepository.findItemsByUserIdAndDeadline(userId, date);

        // 2. 해당 날짜가 생성일(CreatedAt)인 아이템 조회 (00:00:00 ~ 23:59:59)
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
        List<Item> createdItems = itemRepository.findByUser_UserIdAndCreatedAtBetween(
                userId, startDateTime, endDateTime);

        // 3. 두 리스트를 합치고 중복 제거 (ItemId 기준) 후 DTO 변환
        // Stream.concat을 사용하여 두 리스트를 합칩니다.
        List<CalendarDayResponse.EventDetailDto> eventList = Stream.concat(deadlineItems.stream(), createdItems.stream())
                .distinct() // Item 객체의 equals/hashCode가 itemId 기준이라면 중복 제거됨
                .map(item -> CalendarDayResponse.EventDetailDto.builder()
                        .itemId(item.getItemId())
                        .title(item.getTitle())
                        .memo(item.getMemo()) // DTO에 정의한 필드 추가
                        .deadline(item.getDeadline())
                        .createdAt(item.getCreatedAt())
                        .tag(item.getItemTags() != null ?
                                item.getItemTags().stream()
                                        .map(itemTag -> itemTag.getTag().getTagName()) // 태그의 이름 추출
                                        .collect(Collectors.toList())
                                : Collections.emptyList()) // 태그가 없으면 빈 리스트 반환
                        .importance(item.isImportance())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        // 4. 최종 결과 반환
        return CalendarDayResponse.builder()
                .selectedDate(date)
                .eventList(eventList)
                .build();
    }
}
