package com.gdg.linking.domain.item.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 대량 삭제 요청 DTO")
public class ItemDeleteRequest {

    @Schema(description = "삭제할 아이템 ID들", example = "[1, 2, 3]")
    private List<Long> itemIds;


}
