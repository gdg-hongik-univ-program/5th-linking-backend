package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
import com.gdg.linking.domain.item.dto.response.*;

import java.util.List;
import java.util.Map;

public interface ItemService {


    ItemCreateResponse createItem(ItemCreateRequest request,Long userId);

    ItemGetResponse getItem(Long itemId);

    ItemUpdateResponse updateItem(ItemUpdateRequest request);

    ItemDeleteResponse deleteItem(Long itemId, Long userId);

    List<ItemGetResponse> getMyItems(Long userId);


    void addRelatedLink(Long fromId, Long toId);

    void disconnectItems(Long fromId, Long toId, Long userId);

    Map<String, List<RelatedItemResponse>> getAllRelatedLinks(Long itemId);

    List<ItemGetResponse> getByFolderId(Long folderId);
}
