package com.road_journey.road_journey.items.entity;

import com.road_journey.road_journey.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_item")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private boolean isSelected;
    private int growthPoint;
    private int growthLevel;
}