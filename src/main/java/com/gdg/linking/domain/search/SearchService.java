package com.gdg.linking.domain.search;


import com.gdg.linking.domain.search.dto.response.SearchResponse;

public interface SearchService {

    SearchResponse search(String keyword, int page, int size,Long userId);
}
