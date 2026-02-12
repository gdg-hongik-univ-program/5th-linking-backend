package com.gdg.linking.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 대량 삭제 요청 DTO")
public class FolderDeleteRequest {
    @Schema(description = "삭제할 폴더 ID들", example = "[1, 2, 3]")
    private List<Long> folderIds;
}


