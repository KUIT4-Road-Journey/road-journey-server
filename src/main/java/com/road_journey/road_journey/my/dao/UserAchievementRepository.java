package com.road_journey.road_journey.my.dao;

import com.road_journey.road_journey.my.domain.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    // 특정 사용자의 모든 업적 조회
    List<UserAchievement> findByUser_UserId(Long userId);

    // 특정 사용자의 특정 업적 조회
    Optional<UserAchievement> findByUser_UserIdAndAchievementId(Long userId, Long achievementId);

    // 특정 카테고리의 업적 조회
    List<UserAchievement> findByUser_UserIdAndAchievementCategory(Long userId, String category);
}
