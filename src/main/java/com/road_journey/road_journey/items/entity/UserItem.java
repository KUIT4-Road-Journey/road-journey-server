package com.road_journey.road_journey.items.entity;

import com.road_journey.road_journey.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_item")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userItemId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private boolean isSelected;

    @Column
    private Long growthPoint;

    @Column
    private Long growthLevel;

    @Column(nullable = false, length = 50)
    private String status = "active";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}