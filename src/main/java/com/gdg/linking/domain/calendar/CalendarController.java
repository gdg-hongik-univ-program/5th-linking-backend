package com.gdg.linking.domain.calendar;


import com.gdg.linking.domain.calendar.dto.request.CalendarPageRequest;
import com.gdg.linking.domain.calendar.dto.response.CalendarMonthResponse;
import com.gdg.linking.domain.calendar.dto.response.CalendarPageResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@Tag(name = "Calendar", description = "캘린더 및 일정 관리 API") // API 그룹화
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1") // 예시 경로
public class CalendarController {

    private final CalendarService calendarService;


    @Operation(
            summary = "캘린더 데이터 조회",
            description = "nav바에서 클릭 하자마자 특정 연도와 월을 입력받아 해당 월의 요약 정보(점)와 상세 일정 리스트를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarPageResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요 (@LoginCheck)", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content)
    })
    @LoginCheck
    @PostMapping("/calendar")
    public ResponseEntity<CalendarPageResponse> getCalendar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "조회하고자 하는 연도와 월 정보")
            @RequestBody CalendarPageRequest request,
            @Parameter(hidden = true) HttpSession session){ // 세션은 문서에서 숨김

        Long userId = getLoginUserId(session);

         CalendarPageResponse response = calendarService.getCalendarPageData(request, userId);
         return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "월별 캘린더 데이터 조회",
            description = "월을 옮길때마다 특정 년도와 월을 입력받습니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarPageResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요 (@LoginCheck)", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content)
    })
    @LoginCheck
    @PostMapping("/calendar/mon")
    public ResponseEntity<CalendarMonthResponse> getMonCalendar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "조회하고자 하는 연도와 월 정보")
            @RequestBody CalendarPageRequest request,
            @Parameter(hidden = true) HttpSession session){ // 세션은 문서에서 숨김

        Long userId = getLoginUserId(session);

        CalendarMonthResponse response = calendarService.getCalendarMonthData(request, userId);
        return ResponseEntity.ok(response);

    }


}
