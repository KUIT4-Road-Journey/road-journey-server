package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.Goal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    @EntityGraph(attributePaths = {"periodGoal", "repeatedGoal", "subGoalList"})
    List<Goal> findGoalsByOriginalGoalId(Long originalGoalId);
    @EntityGraph(attributePaths = {"periodGoal", "repeatedGoal", "subGoalList"})
    List<Goal> findByUserIdAndCategoryAndStatus(Long userId, String category, String status);
}
