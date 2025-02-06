package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findGoalsByOriginalGoalId(Long originalGoalId);
    List<Goal> findByUserIdAndCategory(Long userId, String category);
}
