package com.gdg.linking.domain.item.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ItemCreateRequest {


    private String url;

    private String title;

    private Long folderId;

    // json으로 배열을 받음
    private List<String> tags;

    private String memo;

    // JSON에서 "true" 문자열로 와도 boolean으로 자동 변환됩니다.
    private boolean importance;

    // 프론트에서 "2026-01-20"으로 보내는 날짜를 LocalDate로 받습니다.
    private LocalDate deadline;



}
