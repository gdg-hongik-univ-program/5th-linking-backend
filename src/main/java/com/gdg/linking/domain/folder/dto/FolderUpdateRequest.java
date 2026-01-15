package com.gdg.linking.domain.folder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "폴더 수정 요청")
public class FolderUpdateRequest {
    @Schema(description = "변경할 폴더 이름", example = "수정된 폴더명")
    private String folderName; // 수정할 폴더 이름
}
