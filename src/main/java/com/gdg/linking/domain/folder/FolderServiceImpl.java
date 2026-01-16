package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.FolderCreateRequest;
import com.gdg.linking.domain.folder.dto.FolderResponse;
import com.gdg.linking.domain.folder.dto.FolderUpdateRequest;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository; // 유저 확인용

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createFolder(Long userId, FolderCreateRequest request) {
        // 1. 유저 존재 확인
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 2. 폴더 생성 및 저장
        Folder folder = Folder.builder()
                .folderName(request.getFolderName())
                .user(user)
                .build();
        folderRepository.save(folder);
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용이므로 readOnly 설정
    public List<FolderResponse> getFolders(Long userId) {
        // Repository에서 유저 ID로 폴더 목록 조회
        return folderRepository.findByUser_UserId(userId).stream()
                .map(folder -> FolderResponse.builder()
                        .folderId(folder.getFId())
                        .folderName(folder.getFolderName())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void updateFolder(Long folderId, FolderUpdateRequest request) {
        // 1. 대상 폴더 조회
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        // 2. 이름 변경 (Dirty Checking 활용)
        folder.setFolderName(request.getFolderName());
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) {
        // 존재 여부 확인 후 삭제
        if (!folderRepository.existsById(folderId)) {
            throw new RuntimeException("삭제할 폴더가 존재하지 않습니다.");
        }
        folderRepository.deleteById(folderId);
    }
}
