package com.gdg.linking.domain.search.dto.response;


import com.gdg.linking.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResponse {

    private List<SearchItemDto> items;
    private int page;
    private int size;
    private boolean hasNext;

    public static SearchResponse from(Slice<Item> slice) {
        return new SearchResponse(
                slice.getContent().stream()
                        .map(SearchItemDto::from)
                        .toList(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }
}
