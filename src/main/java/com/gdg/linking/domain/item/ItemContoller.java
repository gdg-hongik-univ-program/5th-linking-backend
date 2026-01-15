package com.gdg.linking.domain.item;


import com.gdg.linking.domain.item.dto.request.ItemCreateRequest;
import com.gdg.linking.domain.item.dto.response.ItemCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("item")
public class ItemContoller {

    private final ItemServiceImpl itemService;

    public ItemContoller(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ResponseEntity<ItemCreateResponse> createItem(@RequestBody ItemCreateRequest request){

        ItemCreateResponse response = itemService.createItem(request);

    }
}
