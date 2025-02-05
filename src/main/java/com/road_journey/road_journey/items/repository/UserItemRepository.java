package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    @Query("SELECT ui FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND i.category = :category")
    List<UserItem> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    boolean existsByUserIdAndItemId(Long userId, Long itemId);
}
