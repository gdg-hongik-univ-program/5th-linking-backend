package com.gdg.linking.domain.tag;


import com.gdg.linking.domain.item.dto.request.ItemUpdateRequest;
import com.gdg.linking.domain.item.dto.response.ItemUpdateResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("tag")
@AllArgsConstructor
public class TagController {

    private final TagService service;


}
