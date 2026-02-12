package com.gdg.linking.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 대량 복구 요청 DTO")
public class ItemRestoreRequest {
    @Schema(description = "복구할 아이템 ID들", example = "[1, 2, 3]")
    private List<Long> itemIds;
}