package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(String category);
    List<Item> findByIsSpecialTrue();

    @Query(value = "SELECT * FROM item WHERE category = :category ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Item> findRandomItemsByCategory(String category, int count);

    // 캐릭터 (character) 카테고리 전체 아이템 개수 조회
    @Query("SELECT COUNT(i) FROM Item i WHERE i.category = 'character'")
    Long countAllCharacterItems();

    // 배경 (wallpaper) 카테고리 전체 아이템 개수 조회
    @Query("SELECT COUNT(i) FROM Item i WHERE i.category = 'wallpaper'")
    Long countAllWallpaperItems();

    // 장식품 (ornament) 카테고리 전체 아이템 개수 조회
    @Query("SELECT COUNT(i) FROM Item i WHERE i.category = 'ornament'")
    Long countAllOrnamentItems();
}
