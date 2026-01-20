package com.gdg.linking.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "연결 아이템 정보")
public class RelatedItemRequest {

    @Schema(description = "아이템 고유 번호", example = "1")
    private Long itemId;

    @Schema(description = "연결할 아이템 고유 번호", example = "4")
    private Long linkItemId;

}