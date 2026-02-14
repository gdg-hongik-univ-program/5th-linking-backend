package com.gdg.linking.domain.search;


import com.gdg.linking.domain.item.ItemService;
import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import com.gdg.linking.domain.search.dto.response.SearchResponse;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@RestController
@RequestMapping("search")
@RequiredArgsConstructor
public class SearchController {


    private final SearchService searchService;
    private final ItemService itemService;


    @LoginCheck
    @GetMapping
    @Operation(summary = "내 아이템 목록 조회", description = "필터(upcoming, important, stale, trash, recent,root)를 지원합니다.")
    public ResponseEntity<SearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(value = "filter", required = false) String filter,
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
