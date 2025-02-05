package com.road_journey.road_journey.items.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(name = "item_name", nullable = false, length = 50)
    private String itemName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private Long gold;

    @Column(nullable = false)
    private boolean isSpecial;

    @Column(nullable = false, length = 50)
    private String status = "ACTIVE";  // 기본값 설정

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 엔티티가 저장될 때 자동으로 실행
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 엔티티가 업데이트될 때 자동으로 실행
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Item(Long id, String itemName, String category, String description, Long gold, boolean isSpecial) {
        this.itemId = id;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.gold = gold;
        this.isSpecial = isSpecial;
        this.status = "ACTIVE";
    }
}
