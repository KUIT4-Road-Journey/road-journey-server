package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodGoalId; // 기간목표아이디

    @Column
    private Long goalId; // 목표아이디

    @Column
    private LocalDate startAt; // 시작일

    @Column
    private LocalDate expireAt; // 종료일

    @Column
    private LocalDate periodStartAt; // 주기 시작일

    @Column
    private LocalDate periodExpireAt; // 주기 종료일

    @Column
    private LocalDateTime completedAt; // 달성일

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
