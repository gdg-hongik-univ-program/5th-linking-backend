package com.gdg.linking.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "폴더 정보 응답")
public class FolderResponse {
    @Schema(description = "폴더 ID", example = "1")
    private Long folderId;

    @Schema(description = "폴더 이름", example = "프로젝트 자료")
    private String folderName;
}