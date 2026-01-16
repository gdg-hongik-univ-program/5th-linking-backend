package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.FolderCreateRequest;
import com.gdg.linking.domain.folder.dto.FolderResponse;
import com.gdg.linking.domain.folder.dto.FolderUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Folder", description = "폴더 관련 API")
@RestController
@RequestMapping("folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    @Operation(summary = "폴더 생성", description = "새로운 폴더를 생성합니다.")
    public ResponseEntity<Void> createFolder(@RequestBody FolderCreateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");

        if (userId == null) { // 로그인이 안 되어 있다면 권한 에러 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        folderService.createFolder(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "폴더 목록 조회", description = "사용자의 폴더 목록을 가져옵니다.")
    public ResponseEntity<List<FolderResponse>> getFolders(HttpSession session) {
        // 세션에서 Long 타입의 userId 추출
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FolderResponse> folders = folderService.getFolders(userId);
        return ResponseEntity.ok(folders);
    }

    @PatchMapping("/{folder_id}")
    @Operation(summary = "폴더 이름 수정", description = "기존 폴더의 이름을 수정합니다.")
    public ResponseEntity<Void> updateFolder(
            @PathVariable("folder_id") Long folderId,
            @RequestBody FolderUpdateRequest request) {
        folderService.updateFolder(folderId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folder_id}")
    @Operation(summary = "폴더 삭제", description = "폴더를 삭제합니다.")
    public ResponseEntity<Void> deleteFolder(@PathVariable("folder_id") Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.ok().build();
    }
}
