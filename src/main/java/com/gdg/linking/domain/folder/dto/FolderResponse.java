package com.gdg.linking.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "폴더 정보 응답")
public class FolderResponse {
    @Schema(description = "폴더 ID", example = "1")
    private Long folderId;

    @Schema(description = "폴더 이름", example = "프로젝트 자료")
    private String folderName;

    @Schema(description = "부모 폴더 ID (최상위 폴더면 null)", example = "null")
    private Long parentId;

    // 트리 구조를 위한 코드
    @Builder.Default
    @Schema(description = "하위 폴더 목록")
    private List<FolderResponse> children = new ArrayList<>();
}