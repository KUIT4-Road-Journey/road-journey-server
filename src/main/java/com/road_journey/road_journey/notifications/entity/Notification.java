package com.road_journey.road_journey.notifications.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long relatedId;

    private String message;

    @Column(nullable = false)
    private String status; // "active", "deleted" 상태 구분

    private LocalDateTime createdAt;
}
