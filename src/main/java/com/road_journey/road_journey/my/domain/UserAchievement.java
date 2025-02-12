package com.road_journey.road_journey.my.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_achievement_id")
    private Long userAchievementId;

    @Column(name = "is_reward_accepted", nullable = false)
    private boolean isRewardAccepted;

    @Column(name = "progress", nullable = false)
    private int progress = 0;

}
