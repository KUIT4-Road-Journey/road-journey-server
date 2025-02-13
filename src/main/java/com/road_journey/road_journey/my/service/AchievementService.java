package com.road_journey.road_journey.my.service;

import com.road_journey.road_journey.my.dao.UserAchievementRepository;
import com.road_journey.road_journey.my.domain.AchievementDto;
import com.road_journey.road_journey.my.domain.UserAchievement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final UserAchievementRepository userAchievementRepository;

    public List<AchievementDto> getUserAchievements(Long userId, String category) {
        List<UserAchievement> userAchievements;

        // TODO: 진행도 확인 및 업데이트
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
}
