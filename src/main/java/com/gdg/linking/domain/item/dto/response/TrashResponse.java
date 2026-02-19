package com.gdg.linking.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "휴지통 통합 응답 객체")
public class TrashResponse {
    @Schema(description = "타입 (ITEM 또는 FOLDER)")
    private String type;

    @Schema(description = "고유 ID (itemId 또는 folderId)")
    private Long id;

    @Schema(description = "이름 (제목 또는 폴더명)")
    private String title;

    @Schema(description = "삭제된 날짜")
    private LocalDateTime deletedAt;

    @Schema(description = "원래 소속 폴더 이름 (아이템인 경우)")
    private String folderName;

    @Schema(description = "중요도")
    private boolean importance;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "자식갯수")
    private int childCount;

    @Schema(description = "태그")
    private List<String> tags;

    @Schema(description = "썸네일 저장 링크", example = "[\"요리\", \"레시피\"]")
    private String imageUrl;

}
