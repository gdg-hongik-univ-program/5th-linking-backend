package com.gdg.linking.domain.calendar.dto.request;

import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@Getter
@Setter
@Schema(description = "월별 캘린더 조회를 위한 요청 객체")
public class CalendarPageRequest {

    @Schema(description = "조회하고자 하는 연도", example = "2026")
    private int year;

    @Schema(description = "조회하고자 하는 월 (1~12)", example = "1", minimum = "1", maximum = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    private int month;
}