package com.gdg.linking.domain.item;

import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.folder.FolderRepository;
import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
import com.gdg.linking.domain.item.dto.response.*;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    @Override
    @Transactional
    public ItemCreateResponse createItem(ItemCreateRequest request,Long userId) {


        // 1. 유저 객체의 프록시(가짜 객체)를 가져옴 (DB 쿼리 안 나감)
        User user = userRepository.getReferenceById(userId);
        Folder folder = folderRepository.getReferenceById(request.getFolderId());

        //폴더 Id 추가 아직 안했음
        Item item = Item.builder()
                .user(user)
                .folder(folder)
                .url(request.getUrl())
                .title(request.getTitle())
                .memo(request.getMemo())
                .importance(request.isImportance())
                .deadline(request.getDeadline())
                .build();
        // 3. 리포지토리에 저장
        Item savedItem = itemRepository.save(item);

        // 4. 저장된 정보를 바탕으로 Response 객체 생성 및 반환
        return new ItemCreateResponse(
                savedItem.getItemId(),
                savedItem.getFolder(),
                savedItem.getTitle(),
                savedItem.getMemo(),
                savedItem.isImportance(),
                savedItem.getDeadline()
        );
    }


    //아이템 단일 조회
    @Override
    @Transactional
    public ItemGetResponse getItem(Long itemId) {


        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + itemId));


        ItemGetResponse response = ItemGetResponse.builder()
                .url(item.getUrl())
                .title(item.getTitle())
                .memo(item.getMemo())
                .importance(item.isImportance())
                .deadline(item.getDeadline())
                .createdAt(item.getCreatedAt())
                .build();

        return response;
    }

    @Override
    @Transactional
    public ItemUpdateResponse updateItem(ItemUpdateRequest request) {

        //수정할 아이템 조회 (없으면 예외 발생)
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + request.getItemId()));



        // 데이터 업데이트
        item.update(
                request.getUrl(),
                request.getTitle(),
                request.getMemo(),
                request.isImportance(),
                request.getDeadline()
        );



        // 태그 업데이트 (태그는 보통 별도의 연관관계 처리가 필요합니다)
        // updateTags(item, request.getTags());


        itemRepository.save(item);

        ItemUpdateResponse response = ItemUpdateResponse.builder()
                .itemId(item.getItemId())
                .url(item.getUrl())
                .title(item.getTitle())
                .memo(item.getMemo())
                .importance(item.isImportance())
                .deadline(item.getDeadline())
                // 현재는 태그 기능이 미완성이므로 빈 리스트 혹은 요청받은 태그 리스트를 세팅
                .tags(request.getTags())
                .build();
        return response;

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

        // 삭제
        itemRepository.delete(item);

        ItemDeleteResponse response = ItemDeleteResponse.builder()
                        .itemId(item.getItemId())
                        .message("아이템이 휴지통으로 이동되었습니다.")
                        .build();
        return response;
    }

    //아이템 전체 조회
    @Transactional
    @Override
    public List<ItemGetResponse> getMyItems(Long userId) {
        // 1. 해당 사용자의 아이템 리스트 조회
        List<Item> items = itemRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);

        // 2. Entity -> DTO 변환
        return items.stream()
                .map(item -> ItemGetResponse.builder()
                        .itemId(item.getItemId())
                        .url(item.getUrl())
                        .title(item.getTitle())
                        .memo(item.getMemo())
                        .importance(item.isImportance())
                        .deadline(item.getDeadline())
                        // 태그 기능이 완성되면 여기에 추가 로직 작성
                        .createdAt(item.getCreatedAt())
                        .tags(new ArrayList<>())
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
                        .tags(new ArrayList<>()) // 필요 시 태그 로직 추가
                        .createdAt(item.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return response;
    }

}
