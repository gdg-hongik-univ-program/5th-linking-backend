package com.gdg.linking.domain.profile;

import lombok.Getter;

@Getter
public enum ChessTier {
    PAWN("폰", "PAWN", 1, 10, 0, 1000, "이제 막 수집을 시작한 비기너"),
    KNIGHT("나이트", "KNIGHT" ,11, 20, 1001, 5000, "기동성 있게 정보를 탐색하는 수집가"),
    BISHOP("비숍", "BISHOP",21, 30, 5001, 15000, "정보를 체계적으로 분류하기 시작한 전문가"),
    ROOK("룩", "ROOK",31, 45, 15001, 50000, "든든한 정보의 성벽을 구축한 아카이브 장인"),
    QUEEN("퀸", "QUEEN",46, 59, 50001, 150000, "모든 방향으로 정보를 연결하는 지배자"),
    KING("킹", "KING",60, 60, 150001, Integer.MAX_VALUE, "지식의 왕국을 완성한 독보적인 존재");

    private final String name;
    private final String imageCode;
    private final int minLevel;
    private final int maxLevel;
    private final int minXp;
    private final int maxXp;
    private final String description;

    ChessTier(String name, String imageCode, int minLevel, int maxLevel, int minXp, int maxXp, String description) {
        this.name = name;
        this.imageCode = imageCode;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.minXp = minXp;
        this.maxXp = maxXp;
        this.description = description;
    }

    public static ChessTier findByXp(int totalXp) {
        return java.util.Arrays.stream(values())
                .filter(tier -> totalXp >= tier.minXp && totalXp <= tier.maxXp)
                .findFirst()
                .orElse(PAWN);
    }
}