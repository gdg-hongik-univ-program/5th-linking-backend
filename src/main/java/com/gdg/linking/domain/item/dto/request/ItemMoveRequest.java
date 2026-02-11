package com.gdg.linking.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 폴더 이동 요청 DTO")
public class ItemMoveRequest {

    @Schema(description = "이동할 아이템들의 ID 리스트", example = "[1, 2, 3]")
    private List<Long> itemIds;

    @Schema(description = "이동할 목적지 폴더 ID", example = "10")
    private Long folderId;
}