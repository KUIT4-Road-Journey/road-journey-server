package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @OneToOne
    @JoinColumn(name = "goalId", referencedColumnName = "goalId")
    private Goal goal;

    @Column
    private LocalDate startAt; // 시작일

    @Column
    private LocalDate expireAt; // 종료일

    @Column
    private LocalDate periodStartAt; // 주기 시작일

    @Column
    private LocalDate periodExpireAt; // 주기 종료일

    @Column
    private String status; // 상태

    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column
    private LocalDateTime updatedAt; // 수정일

    public void updatePeriodDate(int repetitionPeriod) {
        periodStartAt = periodExpireAt;
        periodExpireAt = periodStartAt.plusDays(repetitionPeriod);
    }

    public void deactivate() {
        status = "deactivated";
    }

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
