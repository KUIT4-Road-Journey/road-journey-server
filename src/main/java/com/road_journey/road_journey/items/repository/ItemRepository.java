package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryAndIsSpecialFalse(String category);
    List<Item> findByIsSpecialTrue();
}
