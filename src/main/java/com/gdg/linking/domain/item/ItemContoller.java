package com.gdg.linking.domain.item;


import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;
import com.gdg.linking.domain.item.dto.response.ItemDeleteResponse;
import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import com.gdg.linking.domain.item.dto.response.ItemUpdateResponse;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@RestController
@RequestMapping("item")
public class ItemContoller {

    private final ItemServiceImpl itemService;

    public ItemContoller(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @LoginCheck
    @Operation(
            summary = "아이템 생성",
            description = "새로운 아이템을 등록합니다. 세션 정보를 통해 작성자 정보를 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이템 생성 성공",
                    content = @Content(schema = @Schema(implementation = ItemCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음")
    })
    @PostMapping()
    public ResponseEntity<ItemCreateResponse> createItem(@RequestBody ItemCreateRequest request,
                                                         HttpSession session){


        Long userId = getLoginUserId(session);
        ItemCreateResponse response = itemService.createItem(request,userId);

        return ResponseEntity.ok(response);

    }

    @LoginCheck
    @Operation(summary = "내 아이템 전체 조회", description = "현재 로그인한 사용자가 등록한 모든 아이템 목록을 최신순으로 반환합니다.")
    @PostMapping("/me")
    public ResponseEntity<List<ItemGetResponse>> getMyItems(HttpSession session) {

        // 1. 세션에서 로그인한 유저 ID 가져오기
        Long userId = getLoginUserId(session);

        // 2. 서비스 호출 및 결과 반환
        List<ItemGetResponse> responses = itemService.getMyItems(userId);

        return ResponseEntity.ok(responses);
    }



    @LoginCheck
    @Operation(
            summary = "아이템 단건 조회",
            description = "아이템 ID를 이용해 상세 정보를 조회합니다. 로그인 체크가 필요합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 아이템을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "로그인 인증 실패")
    })
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemGetResponse> getItem(@PathVariable("itemId") Long itemId){


        ItemGetResponse response = itemService.getItem(itemId);

        return ResponseEntity.ok(response);
    }


    @LoginCheck
    @Operation(
            summary = "아이템 수정",
            description = "전달받은 JSON 데이터를 바탕으로 기존 아이템 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    @PutMapping
    public ResponseEntity<ItemUpdateResponse> updateItem(
            @RequestBody ItemUpdateRequest request) {


        // Service 단에서 itemId를 기준으로 수정 로직 수행
        ItemUpdateResponse response = itemService.updateItem(request);

        return ResponseEntity.ok(response);
    }


    @LoginCheck
    @Operation(summary = "아이템 삭제", description = "아이템을 휴지통으로 이동(Soft Delete)합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDeleteResponse> deleteItem(
            @PathVariable("itemId") Long itemId,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        ItemDeleteResponse response = itemService.deleteItem(itemId, userId);

        return ResponseEntity.ok(response);
    }



}
