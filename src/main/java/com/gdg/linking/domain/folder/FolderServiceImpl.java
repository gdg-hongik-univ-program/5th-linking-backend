package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.FolderCreateRequest;
import com.gdg.linking.domain.folder.dto.FolderResponse;
import com.gdg.linking.domain.folder.dto.FolderUpdateRequest;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository; // 유저 확인용

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // 2. 부모 폴더 찾기 로직
        Folder parent = null;
        if (request.getParentId() != null) {
            parent = folderRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 폴더를 찾을 수 없습니다."));
        }

        // 3. 폴더 생성 및 저장
        Folder folder = Folder.builder()
                .folderName(request.getFolderName())
                .user(user)
                .parentFolder(parent)
                .build();
        folderRepository.save(folder);
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용이므로 readOnly 설정
    public List<FolderResponse> getFolders(Long userId) {
        // 1. 해당 유저의 모든 폴더를 한 번에 조회
        List<Folder> allFolders = folderRepository.findByUser_UserId(userId);

        // 2. Folder 엔티티를 FolderResponse DTO로 변환하여 Map에 저장
        Map<Long, FolderResponse> responseMap = allFolders.stream()
                .map(folder -> FolderResponse.builder()
                        .folderId(folder.getFId())
                        .folderName(folder.getFolderName())
                        .parentId(folder.getParentFolder() != null ? folder.getParentFolder().getFId() : null)
                        .children(new ArrayList<>())
                        .build())
                .collect(Collectors.toMap(FolderResponse::getFolderId, Function.identity()));

        // 3. 최상위 폴더들을 담을 리스트
        List<FolderResponse> rootFolders = new ArrayList<>();

        // 4. Map을 순회하며 부모-자식 관계 연결
        for (FolderResponse response : responseMap.values()) {
            if (response.getParentId() == null) {
                // 부모가 없으면 최상위 폴더 리스트에 추가
                rootFolders.add(response);
            } else {
                // 부모가 있으면 부모의 children 리스트에 자기 자신을 추가
                FolderResponse parent = responseMap.get(response.getParentId());
                if (parent != null) {
                    parent.getChildren().add(response);
                }
            }
        }

        return rootFolders;
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
