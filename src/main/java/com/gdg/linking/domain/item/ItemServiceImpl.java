package com.gdg.linking.domain.item;

import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class ItemServiceImpl implements ItemService{


    @Override
    @Transactional
    public ItemCreateResponse createItem(ItemCreateRequest request) {
        return null;
    }




}
