package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repeatedGoalId; // 기간목표아이디

    @Column(name = "goal_id2")
    private Long goalId; // 목표아이디

    @Column(name = "field")
    private LocalDateTime startAt; // 시작일

    @Column(name = "field3")
    private LocalDateTime expireAt; // 종료일

    @Column(name = "field2")
    private LocalDateTime completedAt; // 달성일

    @Column(name = "status")
    private String status; // 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일
}
