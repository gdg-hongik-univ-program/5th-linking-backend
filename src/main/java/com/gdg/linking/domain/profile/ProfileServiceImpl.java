package com.gdg.linking.domain.profile;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import com.gdg.linking.domain.profile.dto.ProfileResponse;
import com.gdg.linking.domain.user.User;
import com.gdg.linking.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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

        user.setTotalXp(user.getTotalXp() + amount);

        // 현재 XP에 맞는 티어 정보 가져오기
        ChessTier tier = ChessTier.findByXp(user.getTotalXp());

        // 티어 내에서 레벨 계산
        int newLevel = calculateLevel(user.getTotalXp(), tier);

        user.setLevel(newLevel);
        user.setProfileImage(tier.getImageCode()); // 등급에 맞는 이미지 코드를 DB에 저장
    }

    private int calculateLevel(int totalXp, ChessTier tier) {
        if (tier == ChessTier.KING) return 60; // 만렙 고정

        // 티어별 레벨 범위와 XP 범위를 활용한 선형 계산
        double xpRange = tier.getMaxXp() - tier.getMinXp();
        double levelRange = tier.getMaxLevel() - tier.getMinLevel();
        double progress = (totalXp - tier.getMinXp()) / xpRange;

        return tier.getMinLevel() + (int) (progress * levelRange);
    }
}
