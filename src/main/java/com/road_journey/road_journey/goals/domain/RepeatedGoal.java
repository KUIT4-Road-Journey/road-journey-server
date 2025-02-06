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

    @Column
    private Long goalId; // 목표아이디

    @Column
    private int repetitionPeriod; // 반복일

    @Column
    private int repetitionNumber; // 반복횟수

    @Column
    private int completedCount; // 성공횟수

    @Column
    private int failedCount; // 실패횟수

    @Column
    private String status; // 상태

    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column
    private LocalDateTime updatedAt; // 수정일

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
