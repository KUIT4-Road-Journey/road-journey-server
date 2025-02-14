package com.road_journey.road_journey.my.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.goals.service.GoalService;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import com.road_journey.road_journey.my.dao.UserAchievementRepository;
import com.road_journey.road_journey.my.domain.AchievementDto;
import com.road_journey.road_journey.my.domain.UserAchievement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);
    private final UserAchievementRepository userAchievementRepository;
    private final GoalService goalService;
    private final UserItemRepository userItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<AchievementDto> getUserAchievements(Long userId, String category) {
        List<UserAchievement> userAchievements;

        updateAchievementProgress(userId);

        if ("all".equalsIgnoreCase(category)) {
            userAchievements = userAchievementRepository.findByUser_UserId(userId);
        } else {
            userAchievements = userAchievementRepository.findByUser_UserIdAndAchievementCategory(userId, category);
        }

        return userAchievements.stream()
                .map(AchievementDto::from)
                .collect(Collectors.toList());
    }

    public boolean acceptReward(Long achievementId, Long userId) {
        UserAchievement userAchievement = userAchievementRepository.findByUser_UserIdAndAchievementId(userId, achievementId)
                .orElseThrow(() -> new RuntimeException("해당 업적을 찾을 수 없습니다."));

        // 이미 보상을 받았는지 체크
        if (userAchievement.isRewardAccepted()) {
            throw new IllegalStateException("이미 보상을 받은 업적입니다.");
        }

        // 업적이 완료되지 않았는지 체크
        if (userAchievement.getProgress() < 100) {
            throw new IllegalStateException("업적을 완료하지 않았습니다. 진행도를 100%로 채우세요.");
        }

        // 보상을 수락
        userAchievement.setRewardAccepted(true);
        userAchievementRepository.save(userAchievement);

        // TODO: 경험치, 골드 업데이트 로직 추가
        return true;
    }

    @Transactional
    private void updateAchievementProgress(Long userId) {
        List<UserAchievement> userAchievements = userAchievementRepository.findByUser_UserId(userId);

        for (UserAchievement userAchievement : userAchievements) {
            String category = userAchievement.getAchievement().getCategory();
            Long achievementId = userAchievement.getAchievement().getId();
            int newProgress = calculateProgress(userId, category, achievementId);

            userAchievement.setProgress(Math.min(100, newProgress));
            userAchievementRepository.save(userAchievement);
        }
    }

    private int calculateProgress(Long userId, String category, Long achievementId) {
        return switch (category) {
            case "goal" -> calculateGoalProgress(userId, achievementId);
            case "character" -> calculateCharacterProgress(userId, achievementId);
            case "item" -> calculateItemProgress(userId, achievementId);
            case "etc" -> calculateGoldProgress(userId, achievementId);
            default -> 0;
        };
    }

    private int calculateGoalProgress(Long userId, Long achievementId) {
        int goalCount = goalService.getCompletedGoalCountOfUser(userId);

        if (achievementId == 1) { // 목표 1개 달성
            return goalCount * 100;
        } else if (achievementId == 2) { // 목표 10개 달성
            return (int) ((goalCount / 10.0) * 100);
        } else if (achievementId == 3) { // 목표 100개 달성
            return (int) ((goalCount / 100.0) * 100);
        }
        return 0;
    }

    private int calculateCharacterProgress(Long userId, Long achievementId) {
        Long userCharacterCount = userItemRepository.countUserCharacterItems(userId);
        Long totalCharacterCount = itemRepository.countAllCharacterItems();

        if (totalCharacterCount == 0) return 0; // 나누기 예외 방지

        if (achievementId == 4) { // 캐릭터 1개 해금
            return (int) (userCharacterCount * 100);
        } else if (achievementId == 5) { // 캐릭터 3개 해금
            return (int) ((userCharacterCount / 3.0) * 100);
        } else if (achievementId == 6) { // 모든 캐릭터 해금
            return (int) ((userCharacterCount / (double) totalCharacterCount) * 100);
        }
        return 0;
    }

    private int calculateItemProgress(Long userId, Long achievementId) {
        Long userItemCount = userItemRepository.countUserNonSpecialItems(userId);
        Long userWallpaperCount = userItemRepository.countUserWallpaperItems(userId);
        Long userOrnamentCount = userItemRepository.countUserOrnamentItems(userId);

        Long totalWallpaperCount = itemRepository.countAllWallpaperItems();
        Long totalOrnamentCount = itemRepository.countAllOrnamentItems();

        if (achievementId == 7) { // 아이템 1개 구매
            log.info("userid"+userId+"item 개수:"+userItemCount);
            return (int) (userItemCount * 100);
        } else if (achievementId == 8) { // 배경 카테고리 모든 아이템 구매
            log.info("userid"+userId+"배경 개수:"+userWallpaperCount);
            return (int) ((userWallpaperCount / (double) totalWallpaperCount) * 100);
        } else if (achievementId == 9) { // 장식품 카테고리 모든 아이템 구매
            log.info("userid"+userId+"장식품 개수:"+userOrnamentCount);
            return (int) (int) ((userOrnamentCount / (double) totalOrnamentCount) * 100);
        }

        return 0;
    }

    private int calculateGoldProgress(Long userId, Long achievementId) {
        Long userGold = userRepository.findGoldByUserId(userId);

        if (achievementId == 10) { // 100000골드 보유
            return (int) ((userGold / 100000.0) * 100);
        } else if (achievementId == 11) { // 1000000골드 보유
            return (int) ((userGold / 1000000.0) * 100);
        }

        return 0;
    }
}
