package com.gdg.linking.domain.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProfileGraphResponse {
    private List<Node> nodes;
    private List<Link> links;

    @Getter
    @Builder
    public static class Node {
        private Long id;
        private String title;
        private int importance; // 노드 크기 조절용 (0 또는 1)
    }

    @Getter
    @Builder
    public static class Link {
        private Long source; // 시작점 itemId
        private Long target; // 끝점 itemId
    }
}
