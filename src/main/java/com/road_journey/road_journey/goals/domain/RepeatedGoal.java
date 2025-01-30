package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.*;
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
public class RepeatedGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repeatedGoalId; // 반복목표아이디

    @Column(name = "goal_id2")
    private Long goalId; // 목표아이디

    @Column(name = "field")
    private int period; // 반복일

    @Column(name = "field2")
    private int count; // 반복횟수

    @Column(name = "field3")
    private int completedCount; // 성공횟수

    @Column(name = "field4")
    private int failedCount; // 실패횟수

    @Column(name = "status")
    private String status; // 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일
}
