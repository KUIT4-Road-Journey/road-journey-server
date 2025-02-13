package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.SubGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {
    @Query("SELECT s FROM SubGoal s WHERE s.goal.goalId = :goalId")
    List<SubGoal> findByGoalId(Long goalId);
}