package com.road_journey.road_journey.notifications.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "related_id", nullable = false)
    private Long relatedId;

    @Column(name = "message", length = 200)
    private String message;

    @Column(nullable = false, length = 50)
    private String status = "active";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Notification(Long userId, String category, Long relatedId, String message) {
        this.userId = userId;
        this.category = category;
        this.relatedId = relatedId;
        this.message = message;
        this.status = "active";
    }
}
