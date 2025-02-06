package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.SubGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {
    List<SubGoal> findByGoalId(Long goalId);
}