package com.gdg.linking.domain.item;

import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.folder.FolderRepository;
import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;
import com.gdg.linking.domain.item.dto.response.ItemDeleteResponse;
import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import com.gdg.linking.domain.item.dto.response.ItemUpdateResponse;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        ItemCreateResponse response = ItemCreateResponse.builder()
                .itemId(savedItem.getItemId())
                .folderId(folder.getFId()) // 위에서 추출한 ID 값 세팅
                .title(savedItem.getTitle())
                .memo(savedItem.getMemo())
                .importance(savedItem.isImportance())
                .deadline(savedItem.getDeadline())
                .build();
        // 4. 저장된 정보를 바탕으로 Response 객체 생성 및 반환
        return response;
    }


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
                        .tags(new ArrayList<>())
                        .build())
                .collect(Collectors.toList());
    }


}
