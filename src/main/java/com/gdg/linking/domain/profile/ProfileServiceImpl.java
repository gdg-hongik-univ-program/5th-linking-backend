package com.gdg.linking.domain.profile;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import com.gdg.linking.domain.item.ItemService;
import com.gdg.linking.domain.notification.NotificationRepository;
import com.gdg.linking.domain.notification.NotificationService;
import com.gdg.linking.domain.profile.dto.ProfileGraphResponse;
import com.gdg.linking.domain.profile.dto.ProfileResponse;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 사용자의 Item 총 개수
        long itemCount = itemRepository.countByUser_UserIdAndStatus(userId, Item.ItemStatus.ACTIVE);

        // 등급 정보 계산
        ChessTier tier = ChessTier.findByXp(user.getTotalXp());

        return ProfileResponse.builder()
                .nickname(user.getNickName())
                .tierName(tier.getName())
                .imageCode(user.getProfileImage())
                .description(tier.getDescription())
                .level(user.getLevel())
                .currentXp(user.getTotalXp())
                .minXp(tier.getMinXp())
                .maxXp(tier.getMaxXp())
                .totalItemCount((int) itemCount)
                .build();
    }

    @Override
    @Transactional
    public void addExperience(Long userId, int amount) {
        User user = userRepository.findById(userId);
        if (user == null) return;

        // 기존 정보(레벨, 티어) 저장
        int oldLevel = user.getLevel();
        ChessTier oldTier = ChessTier.findByXp(user.getTotalXp());

        // XP 추가 및 새로운 정보 계산
        user.setTotalXp(user.getTotalXp() + amount);
        ChessTier newTier = ChessTier.findByXp(user.getTotalXp());
        int newLevel = calculateLevel(user.getTotalXp(), newTier);

        // 유저 정보 업데이트
        user.setLevel(newLevel);

        // 알림 생성 로직
        // 티어 승급 알림 (레벨업보다 상위 개념이므로 먼저 확인)
        if (newTier.ordinal() > oldTier.ordinal()) {
            notificationService.createTierUpNotification(user, newTier.getName());
        }
        // 단순 레벨업 알림 (티어는 그대로인데 레벨만 올랐을 때)
        else if (newLevel > oldLevel) {
            notificationService.createLevelUpNotification(user, newLevel);
        }
    }

    private int calculateLevel(int totalXp, ChessTier tier) {
        if (tier == ChessTier.KING) return 60; // 만렙 고정

        // 티어별 레벨 범위와 XP 범위를 활용한 선형 계산
        double xpRange = tier.getMaxXp() - tier.getMinXp();
        double levelRange = tier.getMaxLevel() - tier.getMinLevel();
        double progress = (totalXp - tier.getMinXp()) / xpRange;

        return tier.getMinLevel() + (int) (progress * levelRange);
    }

    // 옵시디언 뷰
    @Override
    @Transactional(readOnly = true)
    public ProfileGraphResponse getProfileGraph(Long userId) {
        // 유저의 활성화된(ACTIVE) 모든 아이템 조회
        List<Item> userItems = itemRepository.findByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, Item.ItemStatus.ACTIVE);

        // Node 생성
        List<ProfileGraphResponse.Node> nodes = userItems.stream()
                .map(item -> ProfileGraphResponse.Node.builder()
                        .id(item.getItemId())
                        .title(item.getTitle())
                        .importance(item.isImportance() ? 1 : 0)
                        .build())
                .collect(Collectors.toList());

        // Link 생성
        List<ProfileGraphResponse.Link> links = new ArrayList<>();
        for (Item item : userItems) {
            for (Item related : item.getRelatedItems()) {
                if (related.getStatus() == Item.ItemStatus.ACTIVE) {
                    links.add(ProfileGraphResponse.Link.builder()
                            .source(item.getItemId())
                            .target(related.getItemId())
                            .build());
                }
            }
        }

        return ProfileGraphResponse.builder()
                .nodes(nodes)
                .links(links)
                .build();
    }

}
