package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.PeriodGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PeriodGoalRepository extends JpaRepository<PeriodGoal, Long> {
    @Query("SELECT p FROM PeriodGoal p WHERE p.goal.goalId = :goalId")
    Optional<PeriodGoal> findByGoalId(Long goalId);
}
