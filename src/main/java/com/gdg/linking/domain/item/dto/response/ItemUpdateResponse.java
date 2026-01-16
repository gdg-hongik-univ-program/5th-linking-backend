package com.gdg.linking.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "아이템 수정 완료 응답 (전체 데이터)")
public class ItemUpdateResponse {

    @Schema(description = "아이템 ID", example = "12")
    private Long itemId;

    @Schema(description = "수정된 URL", example = "https://...")
    private String url;

    @Schema(description = "수정된 제목", example = "수정된 제목")
    private String title;

    @Schema(description = "수정된 태그 리스트")
    private List<String> tags;

    @Schema(description = "수정된 메모", example = "수정된 메모")
    private String memo;

    @Schema(description = "중요 여부", example = "true")
    private boolean importance;

    @Schema(description = "수정된 기한", example = "2026-01-20")
    private LocalDate deadline;

}