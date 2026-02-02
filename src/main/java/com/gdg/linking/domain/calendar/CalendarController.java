package com.gdg.linking.domain.calendar;


import com.gdg.linking.domain.calendar.dto.request.CalendarPageRequest;
import com.gdg.linking.domain.calendar.dto.response.CalendarMonthResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarDayResponse;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@Tag(name = "Calendar", description = "캘린더 및 일정 관리 API") // API 그룹화
@RestController
@RequiredArgsConstructor
@RequestMapping("calendar") // 예시 경로
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(
            summary = "캘린더 월별 조회",
            description = "년도와 월을 입력받아 캘린더 색깔 표시 전용 API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarMonthResponse.class))), // 수정: MonthResponse로 변경
            @ApiResponse(responseCode = "401", description = "로그인 필요 (@LoginCheck)", content = @Content)
    })
    @LoginCheck
    @GetMapping("mon")
    public ResponseEntity<CalendarMonthResponse> getMonCalendar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "조회하고자 하는 연도와 월 정보")
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month,
            @Parameter(hidden = true) HttpSession session){ // 세션은 문서에서 숨김

        Long userId = getLoginUserId(session);

        CalendarMonthResponse response = calendarService.getCalendarMonthData(year,month, userId);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "캘린더에서 일 클릭시 조회",
            description = "캘린더의 상세 내역을 조회합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarDayResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요 (@LoginCheck)", content = @Content)
    })
    @LoginCheck
    @GetMapping("day")
    public ResponseEntity<CalendarDayResponse> getDayCalendar(

            @RequestParam LocalDate selectedDate,
            @Parameter(hidden = true) HttpSession session){ // 세션은 문서에서 숨김

        Long userId = getLoginUserId(session);

        CalendarDayResponse response = calendarService.getCalendarDayData(selectedDate, userId);
        return ResponseEntity.ok(response);

    }


}
