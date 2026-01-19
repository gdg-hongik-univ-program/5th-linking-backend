package com.gdg.linking.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 생성 요청 객체")
public class ItemCreateRequest {

    @Schema(description = "아이템 링크 URL", example = "https://www.google.com")
    private String url;

    @Schema(description = "아이템 제목", example = "구글 메인 페이지")
    private String title;

    @Schema(description = "저장할 폴더 ID ", example = "1")
    private Long folderId;

    @Schema(description = "메모 내용", example = "나중에 읽어볼 유용한 링크")
    private String memo;

    @Schema(description = "중요 표시 여부", example = "false")
    private boolean importance;

    @Schema(description = "마감 기한 (ISO 날짜 형식)", example = "2026-01-20", type = "string", pattern = "yyyy-MM-dd")
    private LocalDate deadline;
}