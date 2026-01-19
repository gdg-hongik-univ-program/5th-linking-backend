package com.gdg.linking.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 수정 요청 데이터")
public class ItemUpdateRequest {

    @Schema(description = "수정할 아이템 ID", example = "12")
    private Long itemId;

    @Schema(description = "수정할 URL", example = "https://...")
    private String url;

    @Schema(description = "수정된 제목", example = "수정된 제목")
    private String title;

    @Schema(description = "수정된 태그 ID 리스트", example = "[\"1\", \"2\"]")
    private List<String> tags;

    @Schema(description = "수정된 메모", example = "수정된 메모")
    private String memo;

    @Schema(description = "중요 여부 수정", example = "true")
    private boolean importance;

    @Schema(description = "수정된 기한", example = "2026-01-20")
    private LocalDate deadline;


}
