package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.*;
import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import com.gdg.linking.domain.notification.NotificationService;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository; // 유저 확인용

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public FolderResponse createFolder(Long userId, FolderCreateRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        Folder parent = null;
        if (request.getParentId() != null) {
            parent = folderRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 폴더를 찾을 수 없습니다."));
        }

        Folder folder = Folder.builder()
                .folderName(request.getFolderName())
                .user(user)
                .parentFolder(parent)
                .build();

        Folder savedFolder = folderRepository.save(folder);

        return FolderResponse.builder()
                .folderId(savedFolder.getFId())
                .folderName(savedFolder.getFolderName())
                .parentId(savedFolder.getParentFolder() != null ? savedFolder.getParentFolder().getFId() : null)
                .children(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용이므로 readOnly 설정
    public List<FolderResponse> getFolders(Long userId) {
        // 1. 해당 유저의 모든 폴더를 한 번에 조회
        List<Folder> allFolders = folderRepository.findByUser_UserIdAndStatus(userId, Item.ItemStatus.ACTIVE);

        // 1-1.해당 폴더의 아이탬 갯수를 별도로 조회
        List<Object[]> counts = folderRepository.countItemsByUserId(userId);

        // 1-2.
        // 조회한 아이템 갯수 데이터를 Map<FolderId, Count> 형태로 변환하여 메모리에 저장
        Map<Long, Integer> itemCountMap = counts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],                // key: folderId
                        row -> ((Long) row[1]).intValue()    // value: count
                ));

        // 2. Folder 엔티티를 FolderResponse DTO로 변환하여 Map에 저장
        Map<Long, FolderResponse> responseMap = allFolders.stream()
                .map(folder -> convertToResponse(folder, itemCountMap))
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
    public FolderResponse updateFolder(Long folderId, FolderUpdateRequest request) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        folder.setFolderName(request.getFolderName());

        return FolderResponse.builder()
                .folderId(folder.getFId())
                .folderName(folder.getFolderName())
                .parentId(folder.getParentFolder() != null ? folder.getParentFolder().getFId() : null)
                .children(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) {
        // 삭제할 폴더 조회
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("삭제할 폴더가 존재하지 않습니다."));

        // 재귀적으로 상태 변경 실행
        softDeleteRecursive(folder);
    }

    // 하위 구조를 모두 훑으며 상태를 바꾸는 헬퍼 메서드
    private void softDeleteRecursive(Folder folder) {
        // 현재 폴더에 포함된 모든 아이템들을 휴지통으로 이동
        for (Item item : folder.getItems()) {
            item.updateStatus(Item.ItemStatus.ORPHAN); // Item 엔티티의 기존 메서드 활용
        }

        // 현재 폴더 자체를 휴지통으로
        folder.updateStatus(Item.ItemStatus.TRASH);

        // 모든 하위 폴더들에 대해서도 동일한 작업 수행 (재귀 호출)
        for (Folder child : folder.getChildFolders()) {
            softDeleteRecursive(child);
        }
    }

    @Override
    @Transactional
    public void restoreItem(Long itemId, Long userId) {
        // 복구할 아이템 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("아이템을 찾을 수 없습니다."));

        if (!item.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 아이템 자체 복구 (상태 ACTIVE 변경 및 날짜 리셋)
        item.restore();

        // 연관 폴더 복구 로직
        Folder folder = item.getFolder();
        if (folder != null && folder.getStatus() == Item.ItemStatus.TRASH) {
            // 폴더를 ACTIVE 상태로 변경
            folder.restore();

            // 부모 폴더가 여전히 휴지통에 있는 경우
            if (folder.getParentFolder() != null && folder.getParentFolder().getStatus() == Item.ItemStatus.TRASH) {
                // 부모 폴더와의 연결을 끊어 최상위(Root) 폴더로 이동시킴
                folder.setParentFolder(null);
            }
        }

        // 마감일 알림 재예약
        if (item.getDeadline() != null) {
            notificationService.scheduleDeadlineNotifications(item);
        }
    }

    private FolderResponse convertToResponse(Folder folder, Map<Long, Integer> itemCountMap ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = folder.getCreatedAt();

        String displayTime = "";
        if (createdAt != null) {
            if (createdAt.toLocalDate().equals(now.toLocalDate())) {
                // 오늘인 경우: 시:분 (예: 14:30)
                displayTime = createdAt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                // 다른 날인 경우: 연-월-일 (예: 2026년 02월 01일)
                displayTime = createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
            }
        }

        // Map에서 해당 폴더의 아이템 개수를 꺼냄 (없으면 0)
        // DB 접근 없이 메모리 조회
        int itemCount = itemCountMap.getOrDefault(folder.getFId(), 0);

        return FolderResponse.builder()
                .folderId(folder.getFId())
                .folderName(folder.getFolderName())
                .parentId(folder.getParentFolder() != null ? folder.getParentFolder().getFId() : null)
                .createdAt(createdAt)
                .displayTime(displayTime)
                .childCount(folder.getChildFolders().size())
                //item 갯수 조회
                .itemCount(itemCount)
                .children(new ArrayList<>())
                .build();
    }




    @Override
    @Transactional
    public void moveFolders(FolderMoveRequest request, Long userId) {
        // 1. 목표 폴더(Target Parent) 조회 및 검증
        Folder targetParent = null;
        if (request.getParentId() != null) {
            targetParent = folderRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("목표 폴더를 찾을 수 없습니다."));

            // 목표 폴더 권한 확인
            if (!targetParent.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("목표 폴더에 대한 권한이 없습니다.");
            }
        }

        // 2. 이동할 폴더들(Source Folders) 조회
        List<Folder> sourceFolders = folderRepository.findAllById(request.getFolderIds());

        if (sourceFolders.isEmpty()) {
            throw new IllegalArgumentException("이동할 폴더가 선택되지 않았습니다.");
        }

        // 3. 각 폴더에 대해 검증 및 이동 수행
        for (Folder sourceFolder : sourceFolders) {
            // 3-1. 권한 확인 (내 폴더가 맞는지)
            if (!sourceFolder.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("본인의 폴더만 이동할 수 있습니다. ID: " + sourceFolder.getFId());
            }

            // 3-2. 자기 자신으로 이동 방지
            if (targetParent != null && sourceFolder.getFId().equals(targetParent.getFId())) {
                throw new IllegalArgumentException("자기 자신을 부모로 설정할 수 없습니다.");
            }

            // 3-3. 순환 참조 방지 (내가 내 자식 밑으로 들어가는지 체크)
            // 목표 폴더(targetParent)가 현재 이동하려는 폴더(sourceFolder)의 하위인지 확인해야 함
            if (targetParent != null) {
                Folder current = targetParent;
                while (current != null) {
                    if (current.getFId().equals(sourceFolder.getFId())) {
                        throw new IllegalArgumentException("자신의 하위 폴더로는 이동할 수 없습니다. 폴더명: " + sourceFolder.getFolderName());
                    }
                    current = current.getParentFolder();
                }
            }

            // 3-4. 부모 변경 (이동 처리)
            sourceFolder.setParentFolder(targetParent);
        }

        // Dirty Checking으로 인해 트랜잭션 종료 시 일괄 UPDATE 쿼리 발생
    }

    @Override
    @Transactional
    public void deleteFolders(FolderDeleteRequest request, Long userId) {
        // 1. 삭제할 폴더들을 일괄 조회
        List<Folder> folders = folderRepository.findAllById(request.getFolderIds());

        if (folders.isEmpty()) {
            throw new IllegalArgumentException("삭제할 폴더가 선택되지 않았습니다.");
        }

        for (Folder folder : folders) {
            // 2. 권한 확인 (본인 폴더인지)
            if (!folder.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("삭제 권한이 없는 폴더가 포함되어 있습니다. ID: " + folder.getFId());
            }

            // 3. 폴더 상태를 TRASH로 변경 (Recursive)
            // Folder 엔티티의 updateStatus가 하위 아이템들의 상태도 변경하도록 설계되어 있다면 편리합니다.
            softDeleteRecursive(folder);

            // 4. 폴더 내 아이템들의 예약 알림 삭제 (필요 시)
            for (Item item : folder.getItems()) {
                notificationService.deleteReservedNotifications(item.getItemId());
            }
        }
    }

    @Override
    @Transactional
    public void hardDeleteFolders(FolderDeleteRequest request, Long userId) {
        // 1. 요청된 ID 목록으로 폴더 일괄 조회
        List<Folder> folders = folderRepository.findAllById(request.getFolderIds());

        if (folders.isEmpty()) {
            throw new IllegalArgumentException("삭제할 폴더가 선택되지 않았습니다.");
        }

        for (Folder folder : folders) {
            // 2. 권한 확인 (본인 폴더인지)
            if (!folder.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("삭제 권한이 없는 폴더가 포함되어 있습니다. ID: " + folder.getFId());
            }

            // 3. 상태 확인 (휴지통에 있는 폴더만 영구 삭제 가능)
            if (folder.getStatus() != Item.ItemStatus.TRASH) {
                throw new IllegalArgumentException("휴지통에 있는 폴더만 영구 삭제할 수 있습니다. ID: " + folder.getFId());
            }
        }

        // 4. DB에서 영구 삭제 (하위 폴더 및 아이템도 Cascade 설정에 의해 함께 삭제됨)
        folderRepository.deleteAllInBatch(folders);
    }

    // FolderServiceImpl.java

    @Override
    @Transactional
    public void restoreFolders(FolderDeleteRequest request, Long userId) {
        // 1. 요청된 ID 목록으로 폴더 일괄 조회
        List<Folder> folders = folderRepository.findAllById(request.getFolderIds());

        if (folders.isEmpty()) {
            throw new IllegalArgumentException("복구할 폴더가 선택되지 않았습니다.");
        }

        for (Folder folder : folders) {
            // 2. 권한 확인
            if (!folder.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("권한이 없는 폴더가 포함되어 있습니다. ID: " + folder.getFId());
            }

            // 3. 상위 폴더 상태 체크 (고아 폴더 방지 로직)
            // 내 부모가 존재하는데, 그 부모가 휴지통(TRASH)에 있다면? -> 연결을 끊고 최상위로 이동
            if (folder.getParentFolder() != null && folder.getParentFolder().getStatus() == Item.ItemStatus.TRASH) {
                folder.setParentFolder(null);
            }

            // 4. 재귀적으로 복구 수행 (자신 + 하위 아이템 + 하위 폴더)
            restoreRecursive(folder);
        }
    }

    // 내부 헬퍼 메서드: 하위 구조까지 모두 복구
    private void restoreRecursive(Folder folder) {
        // 1. 자신 복구
        folder.restore(); // Folder.java에 정의된 메서드 (status=ACTIVE, deletedAt=null)

        // 2. 내부 아이템들 복구
        for (Item item : folder.getItems()) {
            if (item.getStatus() == Item.ItemStatus.TRASH) {
                item.restore();
                // 마감 알림 등 부가 로직 필요 시 추가 (notificationService 등)
                if (item.getDeadline() != null) {
                    notificationService.scheduleDeadlineNotifications(item);
                }
            }
        }

        // 3. 하위 폴더들 복구 (재귀 호출)
        for (Folder child : folder.getChildFolders()) {
            if (child.getStatus() == Item.ItemStatus.TRASH) {
                restoreRecursive(child);
            }
        }
    }

}
