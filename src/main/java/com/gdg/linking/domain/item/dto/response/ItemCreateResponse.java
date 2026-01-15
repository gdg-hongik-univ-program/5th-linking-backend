package com.gdg.linking.domain.item.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ItemCreateResponse {


        private Long itemId;
        private String title;
        private Long folderId;
        private String memo;
        private boolean importance;
        private LocalDate deadline;

}
