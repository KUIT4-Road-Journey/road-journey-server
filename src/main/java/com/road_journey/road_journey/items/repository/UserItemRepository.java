package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.UserItemInfoDto;
import com.road_journey.road_journey.items.entity.UserItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    @Query("SELECT ui FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND i.category = :category")
    List<UserItem> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    List<UserItem> findByUserId(Long userId);

    List<UserItem> findByUserIdAndIsSelectedTrue(Long userId);

    @Query("SELECT new com.road_journey.road_journey.items.dto.UserItemInfoDto(ui.userItemId, i.itemId, i.itemName, i.category, ui.growthPoint, ui.growthLevel, ui.isSelected) " +
            "FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND ui.isSelected = true")
    List<UserItemInfoDto> findUserItemsWithItemDetails(@Param("userId") Long userId);
}
