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
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId; // 목표아이디

    @Column
    private Long userId; // 사용자아이디

    @Column
    private Long originalGoalId; // 원본목표아이디

    @Column
    private String title; // 목표이름

    @Column
    private int difficulty; // 난이도

    @Column
    private String description; // 상세내용

    @Column
    private boolean isSharedGoal; // 공동목표여부

    @Column
    private boolean isPublic; // 친구공개여부

    @Column
    private String subGoalType; // 하위목표카테고리

    @Column
    private boolean isRewarded; // 보상수락여부

    @Column
    private String sharedStatus; // 공동상태

    @Column
    private int completedStatus; // 완료상태

    @Column
    private LocalDateTime finishedAt; // 완료일

    @Column
    private String status; // 상태

    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column()
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
