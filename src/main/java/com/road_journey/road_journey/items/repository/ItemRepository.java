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
}
