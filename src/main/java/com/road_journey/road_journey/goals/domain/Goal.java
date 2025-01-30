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
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId; // 목표아이디

    @Column(name = "user_id")
    private Long userId; // 사용자아이디

    @Column(name = "goal_id2")
    private Long originalGoalId; // 원본목표아이디

    @Column(name = "field")
    private String title; // 목표이름

    @Column(name = "field2")
    private int difficulty; // 난이도

    @Column(name = "field3")
    private String description; // 상세내용

    @Column(name = "field4")
    private boolean isSharedGoal; // 공동목표여부

    @Column(name = "field5")
    private boolean isPublic; // 친구공개여부

    @Column(name = "field8")
    private String subGoalType; // 하위목표카테고리

    @Column(name = "field9")
    private boolean isRewarded; // 보상수락여부

    @Column(name = "field11")
    private String sharedStatus; // 공동상태

    @Column(name = "field6")
    private int completedStatus; // 완료상태

    @Column(name = "field10")
    private LocalDateTime finishedAt; // 완료일

    @Column(name = "status")
    private String status; // 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일
}
