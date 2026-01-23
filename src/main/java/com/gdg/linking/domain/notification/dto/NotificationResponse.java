package com.gdg.linking.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "알림 정보 응답")
public class NotificationResponse {
    @Schema(description = "알림 ID")
    private Long notificationId;

    @Schema(description = "알림 메시지")
    private String message;

    @Schema(description = "알림 유형 (DEADLINE, CLEANUP 등)")
    private String type;

    @Schema(description = "읽음 여부")
    private boolean isRead;

    @Schema(description = "연결된 아이템 ID (이동용)")
    private Long itemId;

    @Schema(description = "알림 생성 시각")
    private LocalDateTime createdAt;
}
