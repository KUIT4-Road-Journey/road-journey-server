package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepeatedGoalRepository extends JpaRepository<RepeatedGoal, Long> {
    Optional<RepeatedGoal> findByGoalId(Long goalId);
}
