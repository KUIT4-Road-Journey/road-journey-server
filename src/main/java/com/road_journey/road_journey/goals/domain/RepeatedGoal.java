package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @OneToOne
    @JoinColumn(name = "goalId", referencedColumnName = "goalId")
    private Goal goal;

    @Column
    private int repetitionPeriod; // 반복일

    @Column
    private int repetitionNumber; // 반복횟수

    @Column
    private String repetitionHistory; // 기록

    @Column
    private String status; // 상태

    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column
    private LocalDateTime updatedAt; // 수정일

    public void deactivate() {
        status = "deactivated";
    }

    public int getRepeatedCount() {
        return repetitionHistory.length();
    }

    public int getCompletedCount() {
        int count = 0;
        for (int i = 0; i < getRepeatedCount(); i++) {
            if (repetitionHistory.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    public int getFailedCount() {
        int count = 0;
        for (int i = 0; i < getRepeatedCount(); i++) {
            if (repetitionHistory.charAt(i) == '0') {
                count++;
            }
        }
        return count;
    }

    public void recordHistory(boolean isCompleted) {
        if (isCompleted) {
            repetitionHistory = repetitionHistory + "1";
            return;
        }
        repetitionHistory = repetitionHistory + "0";
    }

    public boolean isRepetitionRemaining() {
        return repetitionNumber > repetitionHistory.length();
    }


    public boolean isLastPeriodCompleted() {
        return repetitionHistory.endsWith("1");
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
