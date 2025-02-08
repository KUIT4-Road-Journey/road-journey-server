package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RepeatedGoalRepository extends JpaRepository<RepeatedGoal, Long> {
    @Query("SELECT o FROM RepeatedGoal o WHERE o.goal.goalId = :goalId")
    Optional<RepeatedGoal> findByGoalId(Long goalId);
}
