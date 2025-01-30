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
public class SubGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subGoalId;

    // @ManyToOne
    // @JoinColumn(name = "goal_id2")
    @Column(name = "goal_id2")
    private Long goalId;

    @Column(name = "field2")
    private int index; // 순번

    @Column(name = "field3")
    private String description; // 내용

    @Column(name = "field4")
    private int isCompleted; // 달성여부

    @Column(name = "field")
    private int difficulty; // 난이도

    @Column(name = "status")
    private String status; // 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일
}
