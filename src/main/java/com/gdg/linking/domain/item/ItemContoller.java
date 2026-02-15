package com.gdg.linking.domain.item;


import com.gdg.linking.domain.item.dto.request.*;
import com.gdg.linking.domain.item.dto.response.*;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@RestController
@RequestMapping("item")
public class ItemContoller {

    private final ItemService itemService;

    public ItemContoller(ItemService itemService) {
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
    @GetMapping("")
    @Operation(summary = "내 아이템 목록 조회 및 검색",
            description = "필터(upcoming, important, stale, trash)와 검색어(keyword)를 조합하여 조회합니다.")
    public ResponseEntity<List<ItemGetResponse>> getMyItems(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "keyword", required = false) String keyword, // 추가됨
            HttpSession session) {

        Long userId = getLoginUserId(session);

        // keyword 파라미터 추가 전달
        List<ItemGetResponse> response = itemService.getMyItems(userId, filter, keyword);

        return ResponseEntity.ok(response);
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
    @Operation(summary = "아이템 대량 삭제", description = "아이템을 휴지통으로 이동(Soft Delete)합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("")
    public ResponseEntity<ItemDeleteResponse> deleteItems(
            @RequestBody ItemDeleteRequest request,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        ItemDeleteResponse response = itemService.deleteItems(request, userId);

        return ResponseEntity.ok(response);
    }

    // 휴지통 전체 비우기
    @LoginCheck
    @DeleteMapping("/trash/all")
    @Operation(summary = "휴지통 전체 비우기")
    public ResponseEntity<Void> clearTrash(HttpSession session) {
        Long userId = getLoginUserId(session);
        itemService.emptyTrash(userId);
        return ResponseEntity.ok().build();
    }


    @LoginCheck
    @Operation(
            summary = "폴더별 아이템 목록 조회",
            description = "특정 폴더(folderId)에 속한 모든 아이템 리스트를 가져옵니다. 로그인한 사용자의 권한 확인이 필요합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ItemGetResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 폴더 ID"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("folder/{folderId}")
    public ResponseEntity<List<ItemGetResponse>> getFolderItem(@PathVariable Long folderId,
                                                               @Parameter(hidden = true) HttpSession session){

        Long userId = getLoginUserId(session);
        List<ItemGetResponse> response = itemService.getByFolderId(folderId);

        return ResponseEntity.ok(response);
    }

    @LoginCheck
    @Operation(summary = "연관 링크 목록 조회", description = "특정 아이템에 연결된 모든 연관 링크를 조회합니다.")
    @GetMapping("/connect/{itemId}")
    public ResponseEntity<List<RelatedItemResponse>> linkItem(@PathVariable Long itemId){

        List<RelatedItemResponse> response = itemService.getAllRelatedLinks(itemId);

        return ResponseEntity.ok(response);
    }
    @LoginCheck
    @Operation(summary = "연관 링크 등록", description = "두 아이템 사이에 연관 관계를 생성합니다.")
    @PostMapping("/connect")
    public ResponseEntity<List<RelatedItemResponse>> linkItem(@RequestBody RelatedItemRequest request){

        itemService.addRelatedLink(request.getItemId(), request.getLinkItemId());


        // 2. 갱신된 전체 리스트 조회 (Service에서 처리 권장)
        List<RelatedItemResponse> updatedList = itemService.getAllRelatedLinks(request.getItemId());


        return ResponseEntity.ok(updatedList);
    }

    @LoginCheck
    @Operation(summary = "연관 링크 해제", description = "두 아이템 사이의 연관 관계를 삭제합니다.")
    @DeleteMapping("/connect")
    public ResponseEntity<List<RelatedItemResponse>> linkItem(@RequestBody RelatedDeleteRequest request,
                                         HttpSession session){

        Long userId = getLoginUserId(session);
        itemService.disconnectItems(request.getItemId(),request.getLinkItemId(),userId);

        // 2. 갱신된 전체 리스트 조회 (Service에서 처리 권장)
        List<RelatedItemResponse> updatedList = itemService.getAllRelatedLinks(request.getItemId());


        return ResponseEntity.ok(updatedList);
    }

    @LoginCheck
    @Operation(summary = "아이템 중요도 수정", description = "아이템의 중요도(importance) 상태를 토글하거나 직접 설정합니다.")
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemUpdateResponse> updateImportance(
            @PathVariable("itemId") Long itemId,
            @RequestBody Map<String, Boolean> payload,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        boolean importance = payload.get("importance");

        ItemUpdateResponse response = itemService.updateImportance(itemId, userId, importance);

        return ResponseEntity.ok(response);
    }

    @LoginCheck
    @Operation(summary = "아이템 중요도 수정", description = "아이템의 중요도(importance) 상태를 토글하거나 직접 설정합니다.")
    @PatchMapping("/folder/{itemId}")
    public ResponseEntity<ItemUpdateResponse> updateItemFolder(
            @PathVariable("itemId") Long itemId,
            @RequestBody Map<String, Boolean> payload,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        boolean importance = payload.get("importance");

        ItemUpdateResponse response = itemService.updateImportance(itemId, userId, importance);

        return ResponseEntity.ok(response);
    }

    @LoginCheck
    @Operation(summary = "아이템 폴더 이동", description = "여러 아이템을 특정 폴더로 일괄 이동합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이동 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (폴더 없음, 권한 없음 등)"),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @PatchMapping("/move")
    public ResponseEntity<Void> moveItems(
            @RequestBody ItemMoveRequest request,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        itemService.moveItemsToFolder(request, userId);

        return ResponseEntity.ok().build();
    }

    //대량 아이템 복구 코드
    @LoginCheck
    @PostMapping("/restore")
    @Operation(summary = "아이템 대량 복구", description = "여러 개의 아이템을 한꺼번에 복구합니다. (관련 폴더도 필요 시 함께 복구됨)")
    public ResponseEntity<Void> restoreItems(
            @RequestBody ItemRestoreRequest request,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        itemService.restoreItems(request, userId);

        return ResponseEntity.ok().build();
    }

    @LoginCheck
    @DeleteMapping("/trash")
    @Operation(summary = "휴지통 아이템 선택 대량 영구 삭제", description = "휴지통에 있는 아이템 중 선택한 것들을 영구적으로 삭제합니다. (복구 불가)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "영구 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (본인 아이템 아님, 휴지통 아님 등)")
    })
    public ResponseEntity<Void> deleteItemsFromTrash(
            @RequestBody ItemDeleteRequest request,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        itemService.hardDeleteItems(request, userId);

        return ResponseEntity.ok().build();
    }

}
