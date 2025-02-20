package com.road_journey.road_journey.items.entity;

import jakarta.persistence.PreUpdate;

import java.util.Map;

public class UserItemListener {

    @PreUpdate
    public void beforeUpdate(UserItem userItem) {
        if (userItem.getGrowthPoint() == null) return;

        // 성장 조건 (레벨업에 필요한 포인트)
        Map<Integer, Long> levelThresholds = Map.of(
                1, 200L,
                2, 1500L,
                3, 5000L
        );

        int currentLevel = Math.toIntExact(userItem.getGrowthLevel());
        while (levelThresholds.containsKey(currentLevel) &&
                userItem.getGrowthPoint() >= levelThresholds.get(currentLevel)) {
            userItem.setGrowthPoint(userItem.getGrowthPoint() - levelThresholds.get(currentLevel));
            userItem.setGrowthLevel(userItem.getGrowthLevel() + 1);
            currentLevel++;
        }
    }
}
