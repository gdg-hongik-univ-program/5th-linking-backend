package com.gdg.linking.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Schema(description = "아이템 삭제 응답")
public class ItemDeleteResponse {

    @Schema(description = "삭제된 아이템 ID", example = "12")
    private Long itemId;

    @Schema(description = "결과 메시지", example = "아이템이 휴지통으로 이동되었습니다.")
    private String message;
}