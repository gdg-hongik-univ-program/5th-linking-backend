package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;

public interface ItemService {


    ItemCreateResponse createItem(ItemCreateRequest request);
}
