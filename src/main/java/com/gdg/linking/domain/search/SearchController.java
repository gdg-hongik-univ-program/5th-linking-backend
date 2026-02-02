package com.gdg.linking.domain.search;


import com.gdg.linking.domain.search.dto.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("search")
@RequiredArgsConstructor
public class SearchController {


    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        //객체 구조 고민중
        //SearchResponse = searchService.search(keyword, page, size);
        return ResponseEntity.ok().build();

    }

}
