package com.gdg.linking.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "폴더 이동 요청 DTO")
public class FolderMoveRequest {

    @Schema(description = "이동할 폴더들의 ID 리스트", example = "[1, 2, 3]")
    private List<Long> folderIds;

    @Schema(description = "이동할 곳의 상위 폴더 ID (최상위로 이동하려면 null)", example = "10")
    private Long parentId;
}