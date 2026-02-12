package com.gdg.linking.domain.tag;

import com.gdg.linking.domain.tag.dto.TagStatResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TagService {
    List<TagStatResponse> getTop5TagStats(Long userId);
}
