package com.gdg.linking.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
    private String nickname;

    @Schema(description = "티어(계급) 이름")
    private String tierName;

    @Schema(description = "사용자 프로필 이미지 코드")
    private String imageCode;

    @Schema(description = "티어 수식어")
    private String description;

    private int level;

    @Schema(description = "현재 누적 XP")
    private int currentXp;

    @Schema(description = "현재 티어 시작 XP")
    private int minXp;

    @Schema(description = "현재 티어 목표 XP")
    private int maxXp;

    @Schema(description = "저장된 아이템 총 개수")
    private int totalItemCount;
}