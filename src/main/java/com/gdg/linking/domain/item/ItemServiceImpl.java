package com.gdg.linking.domain.item;

import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.folder.FolderRepository;
import com.gdg.linking.domain.item.dto.request.*;
import com.gdg.linking.domain.item.dto.response.*;
import com.gdg.linking.domain.notification.NotificationService;
import com.gdg.linking.domain.profile.ProfileService;
import com.gdg.linking.domain.tag.ItemTag;
import com.gdg.linking.domain.tag.Tag;
import com.gdg.linking.domain.tag.TagRepository;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final FolderRepository folderRepository;

    private final NotificationService notificationService;

    private final ProfileService profileService;

    private final TagRepository tagRepository;


    @Override
    @Transactional
    public ItemCreateResponse createItem(ItemCreateRequest request,Long userId) {


        // 1. 유저 객체의 프록시(가짜 객체)를 가져옴 (DB 쿼리 안 나감)
        User user = userRepository.getReferenceById(userId);

        Folder folder = null; // 기본값은 null

        // 요청에 folderId가 포함되어 있을 때만 DB에서 조회
        if (request.getFolderId() != null) {
            folder = folderRepository.findByFIdAndUser(request.getFolderId(), user)
                    .orElse(null); // 못 찾아도 에러 내지 않고 null 유지
        }
        if (folder != null && folder.getStatus() == Item.ItemStatus.TRASH) {
            throw new IllegalArgumentException("휴지통에 있는 폴더에는 아이템을 추가할 수 없습니다.");
        }


        Item item = Item.builder()
                .user(user)
                .folder(folder)
                .status(Item.ItemStatus.ACTIVE)
                .url(request.getUrl())
                .title(request.getTitle())
                .memo(request.getMemo())
                .importance(request.isImportance())
                .deadline(request.getDeadline())
                .build();

        // 2. 태그 처리 로직 (핵심)
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for (String tagName : request.getTags()) {
                // 공백 제거
                String refinedTagName = tagName.trim();
                if(refinedTagName.isEmpty()) continue;

                // A. 태그 찾기 or 생성하기 (Find or Create)
                Tag tag = tagRepository.findByTagName(refinedTagName)
                        .orElseGet(() -> tagRepository.save(
                                Tag.builder().tagName(refinedTagName).build()
                        ));

                // B. ItemTag 연결 객체 생성 (ItemTag.createItemTag 사용)
                // 주의: ItemTag 엔티티에 createItemTag 메서드가 static으로 있어야 함
                ItemTag itemTag = ItemTag.createItemTag(item, tag);

                // C. 아이템의 리스트에 추가
                item.addItemTag(itemTag);
            }
        }

        // 3. 리포지토리에 저장
        Item savedItem = itemRepository.save(item);
        ItemCreateResponse response = ItemCreateResponse.builder()
                .itemId(savedItem.getItemId())
                .folderName(folder != null ? folder.getFolderName() : null)
                .folderId(folder != null ? folder.getFId() : null)
                .title(savedItem.getTitle())
                .memo(savedItem.getMemo())
                .importance(savedItem.isImportance())
                .deadline(savedItem.getDeadline())
                .tags(savedItem.getItemTags().stream()
                        .map(it -> it.getTag().getTagName())
                        .collect(Collectors.toList()))
                .build();

        // Item 생성 시 XP 증가
        profileService.addExperience(userId, 10); // 기본 링크 저장 (+10 XP)
        if (request.getMemo() != null && request.getMemo().length() >= 25) {
            profileService.addExperience(userId, 25); // 25자 이상 메모 작성 (+25 XP)
        }

        notificationService.scheduleDeadlineNotifications(savedItem);

        // 4. 저장된 정보를 바탕으로 Response 객체 생성 및 반환
        return response;
    }


    //아이템 단일 조회
    @Override
    @Transactional
    public ItemGetResponse getItem(Long itemId) {


        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + itemId));


        ItemGetResponse response = ItemGetResponse.builder()
                .itemId(item.getItemId())
                .url(item.getUrl())
                .folderName(item.getFolder() != null ? item.getFolder().getFolderName() : "미지정")// 위에서 추출한 Name 값 세팅
                .folderId(item.getFolder() != null ? item.getFolder().getFId() : null)// 위에서 추출한 Name 값 세팅
                .title(item.getTitle())
                .memo(item.getMemo())
                .importance(item.isImportance())
                .deadline(item.getDeadline())
                .createdAt(item.getCreatedAt())
                .tags(item.getItemTags().stream()
                        .map(it -> it.getTag().getTagName())
                        .collect(Collectors.toList()))
                .updatedAt(item.getUpdatedAt())
                .build();

        return response;
    }

    @Override
    @Transactional
    public ItemUpdateResponse updateItem(ItemUpdateRequest request) {

        //수정할 아이템 조회 (없으면 예외 발생)
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + request.getItemId()));

        LocalDate oldDeadline = item.getDeadline();

        // 데이터 업데이트
        item.update(
                request.getUrl(),
                request.getTitle(),
                request.getMemo(),
                request.isImportance(),
                request.getDeadline()
        );


        if (oldDeadline != null && !oldDeadline.equals(request.getDeadline())) {
            notificationService.deleteReservedNotifications(item.getItemId());
            notificationService.scheduleDeadlineNotifications(item);
        }



        // 태그 업데이트 (태그는 보통 별도의 연관관계 처리가 필요합니다)
        // updateTags(item, request.getTags());
        // 3. 태그 업데이트 (전체 삭제 후 재등록 방식)
        updateTags(item, request.getTags());

        itemRepository.save(item);
        // 4. 응답 DTO 생성
        return ItemUpdateResponse.builder()
                .itemId(item.getItemId())
                .url(item.getUrl())
                .title(item.getTitle())
                .memo(item.getMemo())
                .importance(item.isImportance())
                .deadline(item.getDeadline())
                .tags(item.getItemTags().stream()
                        .map(it -> it.getTag().getTagName())
                        .collect(Collectors.toList()))
                .updatedAt(item.getUpdatedAt())
                .build();


    }


    @Override
    @Transactional
    public void emptyTrash(Long userId) {
        // 해당 유저의 아이템 중 상태가 TRASH인 것만 찾아서 한꺼번에 삭제
        List<Item> trashItems = itemRepository.findByUser_UserIdAndStatus(userId, Item.ItemStatus.TRASH);
        List<Folder> trashFolders = folderRepository.findByUser_UserIdAndStatus(userId, Item.ItemStatus.TRASH);

        if (!trashItems.isEmpty()) {
            itemRepository.deleteAllInBatch(trashItems);
        }

        // 휴지통에 있는 폴더들을 일괄 삭제
        if (!trashFolders.isEmpty()) {
            folderRepository.deleteAllInBatch(trashFolders);
        }
    }

    // 내 아이템 조회
    @Override
    @Transactional(readOnly = true) // 조회 최적화
    public List<ItemGetResponse> getMyItems(Long userId, String filter, String keyword) {
        List<Item> items;

        // 검색어가 유효한지 확인 (null이 아니고 빈 문자열도 아님)
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        // 1. 검색어가 있을 때 (AND 조건 적용)
        if (hasKeyword) {
            String searchKeyword = keyword.trim(); // 공백 제거

            if ("upcoming".equals(filter)) {
                // 마감 임박 + 검색
                items = itemRepository.searchUpcomingItems(
                        userId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(7),
                        Item.ItemStatus.ACTIVE,
                        searchKeyword
                );
            } else if ("important".equals(filter)) {
                // 중요 + 검색
                items = itemRepository.searchImportantItems(
                        userId,
                        Item.ItemStatus.ACTIVE,
                        searchKeyword
                );
            } else if ("stale".equals(filter)) {
                // 청소(50일 이상) + 검색
                items = itemRepository.searchStaleItems(
                        userId,
                        LocalDateTime.now().minusDays(50),
                        Item.ItemStatus.ACTIVE,
                        searchKeyword
                );
            } else if ("trash".equals(filter)) {
                // 휴지통 + 검색
                items = itemRepository.searchTrashItems(
                        userId,
                        Item.ItemStatus.TRASH,
                        searchKeyword
                );
            } else {
                items = itemRepository.searchAllItems(
                        userId,
                        Item.ItemStatus.ACTIVE,
                        searchKeyword
                );
            }

        }
        // 2. 검색어가 없을 때 (기존 로직 유지)
        else {
            if ("upcoming".equals(filter)) {
                items = itemRepository.findByUser_UserIdAndDeadlineBetweenAndStatusOrderByDeadlineAsc(
                        userId, LocalDate.now(), LocalDate.now().plusDays(7), Item.ItemStatus.ACTIVE);
            } else if ("important".equals(filter)) {
                items = itemRepository.findByUser_UserIdAndImportanceTrueAndStatus(userId, Item.ItemStatus.ACTIVE);
            } else if ("stale".equals(filter)) {
                items = itemRepository.findStaleItems(
                        userId, LocalDateTime.now().minusDays(50), Item.ItemStatus.ACTIVE);
            } else if ("trash".equals(filter)) {
                items = itemRepository.findByUser_UserIdAndStatusOrderByDeletedAtDesc(userId, Item.ItemStatus.TRASH);
            } else if ("recent".equals(filter)) {
                items = itemRepository.findTop8ByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, Item.ItemStatus.ACTIVE);
            } else if ("root".equals(filter)) {
                items = itemRepository.findByUser_UserIdAndStatusAndFolderIsNullOrderByCreatedAtDesc(userId, Item.ItemStatus.ACTIVE);
            } else {
                items = itemRepository.findByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, Item.ItemStatus.ACTIVE);
            }
        }

        return items.stream()
                .map(item -> ItemGetResponse.builder()
                        .itemId(item.getItemId())
                        .url(item.getUrl())
                        .title(item.getTitle())
                        .folderName(item.getFolder() != null ? item.getFolder().getFolderName() : null)
                        .folderId(item.getFolder() != null ? item.getFolder().getFId() : null)
                        .memo(item.getMemo())
                        .importance(item.isImportance())
                        .deadline(item.getDeadline())
                        .tags(item.getItemTags().stream()
                                .map(it -> it.getTag().getTagName())
                                .collect(Collectors.toList()))
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void addRelatedLink(Long fromId, Long toId) {
        // 본인 확인 로직 등은 생략
        Item fromItem = itemRepository.findById(fromId)
                .orElseThrow(() -> new EntityNotFoundException("기준 아이템 없음"));
        Item toItem = itemRepository.findById(toId)
                .orElseThrow(() -> new EntityNotFoundException("대상 아이템 없음"));

        // 자기 자신을 연결하는 것 방지
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("자기 자신은 연결할 수 없습니다.");
        }

        fromItem.addRelation(toItem);

        // Item 관계 형성 경험치 추가 (+20 XP)
        profileService.addExperience(fromItem.getUser().getUserId(), 20);

    }


    @Override
    @Transactional
    public void disconnectItems(Long fromId, Long toId, Long userId) {
        Item fromItem = itemRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));
        Item toItem = itemRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("대상 아이템을 찾을 수 없습니다."));

        // 권한 확인 (본인 아이템인지)
        if (!fromItem.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        fromItem.removeRelation(toItem); // 리스트에서 제거하면 DB 테이블에서도 삭제됨
    }

    @Override
    @Transactional
    public List<RelatedItemResponse> getAllRelatedLinks(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템이 없습니다."));

        // 1. 내가 연결한 아이템들 (To)
        List<RelatedItemResponse> following = item.getRelatedItems().stream()
                .map(RelatedItemResponse::fromEntity) // RelatedItemResponse의 메서드를 호출!
                .collect(Collectors.toList());

        // 2. 나를 연결한 아이템들 (From)
        List<RelatedItemResponse> followedBy = itemRepository.findItemsLinkingToMe(itemId).stream()
                .map(RelatedItemResponse::fromEntity) // 여기도 마찬가지!
                .collect(Collectors.toList());

        List<RelatedItemResponse> response = new ArrayList<>();
        response.addAll(following);
        response.addAll(followedBy);

        return response;
    }


    //폴더 id로 아이템 조회
    @Transactional
    @Override
    public List<ItemGetResponse> getByFolderId(Long fId) {


        List<Item> items = itemRepository.findByFolder_fIdAndStatus(fId, Item.ItemStatus.ACTIVE);

        List<ItemGetResponse> response = items.stream()
                .map(item -> ItemGetResponse.builder()
                        .itemId(item.getItemId())
                        .url(item.getUrl())
                        .title(item.getTitle())
                        .folderName(item.getFolder() != null ? item.getFolder().getFolderName() : null)
                        .folderId(item.getFolder() != null ? item.getFolder().getFId() : null)
                        .memo(item.getMemo())
                        .importance(item.isImportance())
                        .deadline(item.getDeadline())
                        .tags(item.getItemTags().stream() // ItemTag 리스트를 String 리스트로 변환
                                .map(it -> it.getTag().getTagName())
                                .collect(Collectors.toList()))
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return response;
    }


    // 태그 교체 전용 프라이빗 메서드
    private void updateTags(Item item, List<String> newTagNames) {
        // A. 기존 연결 고리 제거 (CascadeType.ALL과 orphanRemoval=true 설정 시 DB에서도 삭제됨)
        item.getItemTags().clear();

        // B. 새로운 태그 리스트가 있다면 재등록
        if (newTagNames != null && !newTagNames.isEmpty()) {
            for (String name : newTagNames) {
                String trimmedName = name.trim();
                if (trimmedName.isEmpty()) continue;

                // 태그 찾기 or 생성
                Tag tag = tagRepository.findByTagName(trimmedName)
                        .orElseGet(() -> tagRepository.save(
                                Tag.builder().tagName(trimmedName).build()
                        ));

                // 새로운 연결고리 생성 및 추가
                ItemTag itemTag = ItemTag.createItemTag(item, tag);
                item.addItemTag(itemTag);
            }
        }
    }

    @Override
    @Transactional
    public ItemUpdateResponse updateImportance(Long itemId, Long userId, boolean importance) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new IllegalArgumentException("아이템을 찾을 수 없습니다"));
        // 3. 엔티티의 메서드 호출 (상태 변경)
        item.toggleImportance();

        // 4. 응답 DTO 반환 (변경된 상태 반영)
        return ItemUpdateResponse.builder()
                .itemId(item.getItemId())
                .importance(item.isImportance())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    @Transactional
    @Override
    public void moveItemsToFolder(ItemMoveRequest request, Long userId) {
        // 1. 목적지 폴더 조회 및 권한 확인
        // (폴더 ID가 없거나, 본인 폴더가 아니면 예외 발생)
        Folder folder = folderRepository.findById(request.getFolderId())
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));

        if (!folder.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 폴더에 접근 권한이 없습니다.");
        }

        if (folder.getStatus() == Item.ItemStatus.TRASH) {
            throw new IllegalArgumentException("휴지통에 있는 폴더로는 아이템을 이동할 수 없습니다.");
        }

        // 2. 이동할 아이템들 조회
        List<Item> items = itemRepository.findAllById(request.getItemIds());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("이동할 아이템이 선택되지 않았습니다.");
        }

        // 3. 아이템 순회하며 폴더 변경
        for (Item item : items) {
            // 보안 체크: 내 아이템이 맞는지 확인
            if (!item.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("본인의 아이템만 이동할 수 있습니다. ID: " + item.getItemId());
            }

            // 폴더 변경 (Dirty Checking으로 인해 트랜잭션 종료 시 자동 UPDATE 쿼리 발생)
            item.updateFolder(folder);
        }
    }

    @Override
    @Transactional
    public ItemDeleteResponse deleteItems(ItemDeleteRequest request, Long userId) {

        List<Item> items = itemRepository.findAllById(request.getItemIds());
        for (Item item : items) {


            item.updateStatus(Item.ItemStatus.TRASH);

            // 4. 예약된 알림 삭제
            notificationService.deleteReservedNotifications(item.getItemId());
        }

        return ItemDeleteResponse.builder()
                .itemId(null) // 대량 삭제이므로 특정 ID 대신 메시지 중심 응답
                .message(items.size() + "개의 아이템이 휴지통으로 이동되었습니다.")
                .build();
    }


    //아이템 대량 복구
    @Override
    @Transactional
    public void restoreItems(ItemRestoreRequest request, Long userId) {
        // 1. 요청된 ID 목록으로 아이템 일괄 조회
        List<Item> items = itemRepository.findAllById(request.getItemIds());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("복구할 아이템이 선택되지 않았습니다.");
        }

        for (Item item : items) {
            // 2. 권한 확인 (본인 아이템인지)
            if (!item.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("복구 권한이 없는 아이템이 포함되어 있습니다. ID: " + item.getItemId());
            }

            // 3. 아이템 자체 복구 (ACTIVE 상태 변경, 날짜 리셋)
            item.restore(); // Item.java [cite] 에 정의된 메서드 사용

            // 4. 연관 폴더 복구 로직 (단건 복구와 동일한 로직 적용)
            Folder folder = item.getFolder();
            if (folder != null && folder.getStatus() == Item.ItemStatus.TRASH) {
                // 부모 폴더도 함께 ACTIVE로 복구
                folder.restore();

                // 만약 그 부모의 부모(조부모) 폴더가 여전히 TRASH라면 연결 끊기 (최상위로 이동)
                if (folder.getParentFolder() != null && folder.getParentFolder().getStatus() == Item.ItemStatus.TRASH) {
                    folder.setParentFolder(null);
                }
            }

            // 5. 마감일 알림 재예약
            if (item.getDeadline() != null) {
                notificationService.scheduleDeadlineNotifications(item);
            }
        }
    }

    @Override
    @Transactional
    public void hardDeleteItems(ItemDeleteRequest request, Long userId) {
        // 1. 요청된 ID 목록으로 아이템 일괄 조회
        List<Item> items = itemRepository.findAllById(request.getItemIds());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("삭제할 아이템이 선택되지 않았습니다.");
        }

        for (Item item : items) {
            // 2. 권한 확인 (본인 아이템인지)
            if (!item.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("삭제 권한이 없는 아이템이 포함되어 있습니다. ID: " + item.getItemId());
            }

            // 3. 상태 확인 (휴지통에 있는 아이템만 영구 삭제 가능)
            if (item.getStatus() != Item.ItemStatus.TRASH) {
                throw new IllegalArgumentException("휴지통에 있는 아이템만 영구 삭제할 수 있습니다. ID: " + item.getItemId());
            }
        }

        // 4. DB에서 영구 삭제 (일괄 처리로 성능 최적화)
        itemRepository.deleteAllInBatch(items);
    }

}


