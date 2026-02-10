package com.gdg.linking.domain.item;

import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.folder.FolderRepository;
import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
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
        // 폴더 이름으로 찾고, 없으면 즉시 생성하여 저장
        Folder folder = folderRepository.findByFolderNameAndUser(request.getFolderName(), user)
                .orElseGet(() -> {
                    Folder newFolder = Folder.builder()
                            .folderName(request.getFolderName())
                            .user(user)
                            .build();
                    return folderRepository.save(newFolder);
                });



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
                .folderName(folder.getFolderName()) // 위에서 추출한 Name값 세팅
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
                .url(item.getUrl())
                .folderName(item.getFolder() != null ? item.getFolder().getFolderName() : "미지정")// 위에서 추출한 Name 값 세팅
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
    public ItemDeleteResponse deleteItem(Long itemId, Long userId) {

        // 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));

        // 2. 권한 확인
        if (!item.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }


        item.updateStatus(Item.ItemStatus.TRASH); // Item 삭제 시 상태를 ACTIVE에서 TRASH로 변경
        notificationService.deleteReservedNotifications(itemId); // 알림 삭제


        ItemDeleteResponse response = ItemDeleteResponse.builder()
                        .itemId(item.getItemId())
                        .message("아이템이 휴지통으로 이동되었습니다.")
                        .build();
        return response;
    }

    @Override
    @Transactional
    public void hardDeleteOne(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));

        // 본인 확인 및 휴지통 상태 확인
        if (!item.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        if (item.getStatus() != Item.ItemStatus.TRASH) {
            throw new IllegalArgumentException("휴지통에 있는 아이템만 영구 삭제할 수 있습니다.");
        }

        itemRepository.delete(item); // DB에서 제거
    }

    @Override
    @Transactional
    public void emptyTrash(Long userId) {
        // 해당 유저의 아이템 중 상태가 TRASH인 것만 찾아서 한꺼번에 삭제
        List<Item> trashItems = itemRepository.findByUser_UserIdAndStatus(userId, Item.ItemStatus.TRASH);

        if (!trashItems.isEmpty()) {
            itemRepository.deleteAllInBatch(trashItems);
        }
    }

    // 내 아이템 조회
    @Override
    @Transactional
    public List<ItemGetResponse> getMyItems(Long userId, String filter) {
        List<Item> items;

        // 마감 임박 (최신순 + ACTIVE 조건)
        if ("upcoming".equals(filter)) {
            items = itemRepository.findByUser_UserIdAndDeadlineBetweenAndStatusOrderByDeadlineAsc(
                    userId, LocalDate.now(), LocalDate.now().plusDays(7), Item.ItemStatus.ACTIVE);
        }
        // 중요 표시 (최신순 + ACTIVE 조건)
        else if ("important".equals(filter)) {
            items = itemRepository.findByUser_UserIdAndImportanceTrueAndStatus(userId, Item.ItemStatus.ACTIVE);
        }
        // 청소 대상 (최신순 + ACTIVE 조건)
        else if ("stale".equals(filter)) {
            items = itemRepository.findByUser_UserIdAndUpdatedAtBeforeAndStatus(
                    userId, LocalDateTime.now().minusDays(50), Item.ItemStatus.ACTIVE);
        }
        // 휴지통 (TRASH 상태 조회 유지)
        else if ("trash".equals(filter)) {
            items = itemRepository.findByUser_UserIdAndStatusOrderByDeletedAtDesc(userId, Item.ItemStatus.TRASH);
        }
        // 최근 저장 Item 8개 조회 (최신순 + ACTIVE 조건)
        else if ("recent".equals(filter)) {
            items = itemRepository.findTop8ByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, Item.ItemStatus.ACTIVE);
        }
        // 기본 조회 (최신순 + ACTIVE 조건)
        else {
            items = itemRepository.findByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, Item.ItemStatus.ACTIVE);
        }

        return items.stream()
                .map(item -> ItemGetResponse.builder()
                        .itemId(item.getItemId())
                        .url(item.getUrl())
                        .title(item.getTitle())
                        .folderName(item.getFolder() != null ? item.getFolder().getFolderName() : null)
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
    public void restoreItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("아이템을 찾을 수 없습니다."));

        if (!item.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다."); // 보안 체크
        }

        item.restore(); // 상태 ACTIVE 변경 및 날짜 리셋

        if (item.getDeadline() != null) {
            notificationService.scheduleDeadlineNotifications(item);
        }
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


        List<Item> items = itemRepository.findByFolder_fId(fId);

        List<ItemGetResponse> response = items.stream()
                .map(item -> ItemGetResponse.builder()
                        .itemId(item.getItemId())
                        .url(item.getUrl())
                        .title(item.getTitle())
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

}


