package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.FolderCreateRequest;
import com.gdg.linking.domain.folder.dto.FolderMoveRequest;
import com.gdg.linking.domain.folder.dto.FolderResponse;
import com.gdg.linking.domain.folder.dto.FolderUpdateRequest;
import com.gdg.linking.global.aop.LoginCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gdg.linking.global.utils.SessionUtil.getLoginUserId;

@Tag(name = "Folder", description = "폴더 관련 API")
@RestController
@RequestMapping("folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @LoginCheck
    @PostMapping
    @Operation(
            summary = "폴더 생성",
            description = "새로운 폴더를 생성합니다. 최상위 폴더면 null 입력.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "성공적으로 생성됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{ \"folderId\": 2, \"folderName\": \"새 폴더\", \"parentId\": 1, \"children\": [] }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<FolderResponse> createFolder(@RequestBody FolderCreateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");

        FolderResponse response = folderService.createFolder(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @LoginCheck
    @GetMapping
    @Operation(
            summary = "폴더 목록 조회",
            description = "사용자의 폴더 목록을 트리 구조로 가져옵니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "[{ \"folderId\": 1, \"folderName\": \"부모 폴더\", \"parentId\": null, \"children\": [ { \"folderId\": 2, \"folderName\": \"자식 폴더\", \"parentId\": 1, \"children\": [] } ] }]"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<FolderResponse>> getFolders(HttpSession session) {
        // 세션에서 Long 타입의 userId 추출
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");

        List<FolderResponse> folders = folderService.getFolders(userId);
        return ResponseEntity.ok(folders);
    }

    @PatchMapping("/{folder_id}")
    @Operation(
            summary = "폴더 이름 수정",
            description = "기존 폴더의 이름을 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 수정됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{ \"folderId\": 1, \"folderName\": \"수정된 폴더명\", \"parentId\": null, \"children\": [] }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<FolderResponse> updateFolder(
            @PathVariable("folder_id") Long folderId,
            @RequestBody FolderUpdateRequest request) {

        FolderResponse response = folderService.updateFolder(folderId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{folder_id}")
    @Operation(summary = "폴더 삭제", description = "폴더를 삭제합니다.")
    public ResponseEntity<Void> deleteFolder(@PathVariable("folder_id") Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/move")
    @Operation(
            summary = "폴더 이동",
            description = "특정 폴더를 다른 폴더의 하위로 이동시킵니다. (하위 아이템 및 폴더 포함)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이동 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (순환 참조 등)")
            }
    )
    public ResponseEntity<FolderResponse> moveFolder(
            @RequestBody FolderMoveRequest request,
            HttpSession session) {

        Long userId = getLoginUserId(session);
        folderService.moveFolders( request, userId);

        return ResponseEntity.ok().build();
    }
}