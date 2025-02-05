package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    List<UserItem> findByUserIdAndItemCategory(Long userId, String category);
    boolean existsByUserIdAndItemId(Long userId, Long itemId);
}
