package com.gdg.linking.domain.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagStatResponse {
    private String tagName;
    private Long rate;
}
