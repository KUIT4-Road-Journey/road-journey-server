package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.entity.UserItem;
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

    // 사용자가 보유한 캐릭터 아이템 개수 조회
    @Query("SELECT COUNT(ui) FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND i.category = 'character'")
    Long countUserCharacterItems(@Param("userId") Long userId);

    // 사용자가 보유한 일반상점 아이템 개수 조회
    @Query("SELECT COUNT(ui) FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND i.isSpecial = false")
    Long countUserNonSpecialItems(@Param("userId") Long userId);

    // 사용자가 보유한 배경 (wallpaper) 카테고리 아이템 개수 조회
    @Query("SELECT COUNT(ui) FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND i.category = 'wallpaper' AND i.isSpecial = false")
    Long countUserWallpaperItems(@Param("userId") Long userId);

    // 사용자가 보유한 장식품 (ornament) 카테고리 아이템 개수 조회
    @Query("SELECT COUNT(ui) FROM UserItem ui JOIN Item i ON ui.itemId = i.itemId WHERE ui.userId = :userId AND i.category = 'ornament' AND i.isSpecial = false")
    Long countUserOrnamentItems(@Param("userId") Long userId);

    @Query("SELECT ui FROM UserItem ui " +
            "JOIN Item i ON ui.itemId = i.itemId " +
            "WHERE ui.userId = :userId AND ui.isSelected = true AND i.category = 'character'")
    List<UserItem> findSelectedCharacterItems(@Param("userId") Long userId);
}
