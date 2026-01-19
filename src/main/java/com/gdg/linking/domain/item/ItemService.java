package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;
import com.gdg.linking.domain.item.dto.response.ItemDeleteResponse;
import com.gdg.linking.domain.item.dto.response.ItemGetResponse;
import com.gdg.linking.domain.item.dto.response.ItemUpdateResponse;

import java.util.List;

public interface ItemService {


    ItemCreateResponse createItem(ItemCreateRequest request,Long userId);

    ItemGetResponse getItem(Long itemId);

    ItemUpdateResponse updateItem(ItemUpdateRequest request);

    ItemDeleteResponse deleteItem(Long itemId, Long userId);

    List<ItemGetResponse> getMyItems(Long userId);
}
