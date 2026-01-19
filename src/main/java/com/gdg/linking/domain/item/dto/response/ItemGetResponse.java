package com.gdg.linking.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아이템 상세 정보")
public class ItemGetResponse {

    @Schema(description = "아이템 고유 번호", example = "4")
    private Long itemId;


    @Schema(description = "아이템 연결 URL", example = "https://www.youtube.com/watch?v=...")
    private String url;

    @Schema(description = "아이템 제목", example = "최강록의 조리법")
    private String title;

    @Schema(description = "소속된 폴더 ID (미지정 시 null)", example = "1")
    private Long folderId;

    @Schema(description = "연관된 태그 이름 리스트", example = "[\"요리\", \"레시피\"]")
    private List<String> tags;

    @Schema(description = "메모 내용", example = "나중에 꼭 따라하기")
    private String memo;

    @Schema(description = "중요 아이템 여부", example = "true")
    private boolean importance;

    @Schema(description = "완료 및 리마인드 기한", example = "2026-01-20")
    private LocalDate deadline;

}
