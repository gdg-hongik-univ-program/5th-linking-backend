package com.gdg.linking.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "폴더 생성 요청")
public class FolderCreateRequest {
    @Schema(description = "폴더 이름", example = "프로젝트 자료")
    private String folderName; // 생성할 폴더 이름
}
