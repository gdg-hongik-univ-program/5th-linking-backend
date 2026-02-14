package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.request.*;
import com.gdg.linking.domain.item.dto.response.*;

import java.util.List;
import java.util.Map;

public interface ItemService {


    ItemCreateResponse createItem(ItemCreateRequest request,Long userId);

    ItemGetResponse getItem(Long itemId);

    ItemUpdateResponse updateItem(ItemUpdateRequest request);

    ItemDeleteResponse deleteItem(Long itemId, Long userId);

    List<ItemGetResponse> getMyItems(Long userId, String filter, String keyword);
    // 휴지통 개별 아이템 영구 삭제
    void hardDeleteOne(Long itemId, Long userId);

    // 휴지통 전체 비우기
    void emptyTrash(Long userId);

    void restoreItem(Long itemId, Long userId);


    void addRelatedLink(Long fromId, Long toId);

    void disconnectItems(Long fromId, Long toId, Long userId);

    List<RelatedItemResponse> getAllRelatedLinks(Long itemId);


    List<ItemGetResponse> getByFolderId(Long folderId);

    ItemUpdateResponse updateImportance(Long itemId, Long userId, boolean importance);

    void moveItemsToFolder(ItemMoveRequest request, Long userId);


    ItemDeleteResponse deleteItems(ItemDeleteRequest request, Long userId);

    // 아이템 대량 복구
    void restoreItems(ItemRestoreRequest request, Long userId);

    void hardDeleteItems(ItemDeleteRequest request, Long userId);
}
