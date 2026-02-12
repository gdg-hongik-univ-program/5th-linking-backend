package com.gdg.linking.domain.search;


import com.gdg.linking.domain.search.dto.response.SearchResponse;
import com.gdg.linking.global.aop.LoginCheck;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@RestController
@RequestMapping("search")
@RequiredArgsConstructor
public class SearchController {


    private final SearchService searchService;

    @GetMapping
    @LoginCheck
    public ResponseEntity<SearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {
        // 서비스로부터 통합 검색 결과(아이템, 폴더, 태그 섹션별 Slice)를 받아옵니다.
        Long userId = getLoginUserId(session);
        SearchResponse response = searchService.search(keyword, page, size,userId);

        // 성공적으로 조회된 결과를 200 OK와 함께 반환합니다.
        return ResponseEntity.ok(response);
    }

}
