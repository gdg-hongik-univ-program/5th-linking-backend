package com.gdg.linking.domain.item.dto.response;

import com.gdg.linking.domain.folder.Folder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor // 서비스 계층에서 편하게 생성하기 위해 추가
@Schema(description = "아이템 생성 성공 응답 객체")
public class ItemCreateResponse {

        @Schema(description = "생성된 아이템의 고유 ID (PK)", example = "105")
        private Long itemId;

        @Schema(description = "저장된 아이템 제목", example = "구글 메인 페이지")
        private String title;

        @Schema(description = "소속된 폴더 ID (미지정 시 null)", example = "1")
        private Long folderId;

        @Schema(description = "메모 내용", example = "나중에 읽어볼 유용한 링크")
        private String memo;

        @Schema(description = "중요 표시 여부", example = "true")
        private boolean importance;

        @Schema(description = "설정된 마감 기한", example = "2026-01-20")
        private LocalDate deadline;

    public ItemCreateResponse(Long itemId, Folder folder, String title, String memo, boolean importance, LocalDate deadline) {
    }
}