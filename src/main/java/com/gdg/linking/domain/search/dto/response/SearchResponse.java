package com.gdg.linking.domain.search.dto.response;


import com.gdg.linking.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SearchResponse {
    private SearchSlice<ItemSummaryDto> items;   // 검색된 게시글 섹션
    private SearchSlice<FolderSummaryDto> folders; // 검색된 폴더 섹션
    private SearchSlice<TagSummaryDto> tags;      // 검색된 태그 섹션

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
    }

    @Getter
    @Builder
    public static class FolderSummaryDto {
        private Long folderId;
        private String folderName;
    }

    @Getter
    @Builder
    public static class TagSummaryDto {
        private Long tagId;
        private String tagName;
    }
}
