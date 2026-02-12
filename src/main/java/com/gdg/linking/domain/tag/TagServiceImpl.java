package com.gdg.linking.domain.tag;


import com.gdg.linking.domain.tag.dto.TagStatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{

    private final TagRepository tagRepository;

    @Override
    public List<TagStatResponse> getTop5TagStats(Long userId) {
        // DB에서 상위 5개 원본 데이터 추출
        List<Object[]> results = tagRepository.findTop5TagsByUserId(userId, PageRequest.of(0, 5));

        // 데이터가 없는 경우 빈 데이터 세트 반환
        if (results.isEmpty()) {
            return Stream.generate(() -> new TagStatResponse("-", 0L))
                    .limit(5)
                    .collect(Collectors.toList());
        }

        // 상위 5개 태그의 총 사용 횟수 합계 계산
        long totalCount = results.stream()
                .mapToLong(result -> (long) result[1])
                .sum();

        // 비율(%) 계산 및 DTO 변환
        List<TagStatResponse> stats = results.stream()
                .map(result -> {
                    String tagName = (String) result[0];
                    long count = (long) result[1];

                    // 비율 계산: (개별 갯수 / 합계) * 100
                    long percentage = (totalCount > 0) ? (count * 100) / totalCount : 0L;

                    return new TagStatResponse(tagName, percentage);
                })
                .collect(Collectors.toList());

        // 데이터가 5개 미만일 경우 빈 자리 채우기
        while (stats.size() < 5) {
            stats.add(new TagStatResponse("-", 0L));
        }

        return stats;
    }
}
