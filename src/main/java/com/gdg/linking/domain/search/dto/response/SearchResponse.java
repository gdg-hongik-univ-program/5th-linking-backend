package com.gdg.linking.domain.search.dto.response;


import com.gdg.linking.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SearchResponse {
    private SearchSlice<ItemSummaryDto> items;   // 검색된 게시글 섹션
    private SearchSlice<ItemSummaryDto> tags;      // 검색된 태그 관련 아이템 섹션
    private SearchSlice<FolderSummaryDto> folders; // 검색된 폴더 섹션


    @Getter
    @Builder
    public static class SearchSlice<T> {
        private List<T> content;
        private boolean hasNext;
        private int currentPage;
    }

    @Getter
    @Builder
    public static class ItemSummaryDto {
        private Long itemId;
        private String title;
        private String folderName; // 소속 폴더 이름
        private List<String> tags;
        private LocalDate deadline;
    }

    @Getter
    @Builder
    public static class FolderSummaryDto {
        private Long folderId;
        private String folderName;
    }

}
