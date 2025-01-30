package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.UserItemDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemStorageRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemStorageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserItemDto> findUserItems(Long userId, String category) {
        String sql = "SELECT ui.user_item_id AS itemId, i.item_name AS itemName, i.category, " +
                "ui.is_selected AS isSelected, ui.growthPoint, ui.growthLevel, " +
                "i.description AS description, i.gold " +
                "FROM user_item ui " +
                "JOIN item i ON ui.item_id2 = i.item_id " +
                "WHERE ui.user_id = ? AND (i.category = ? OR ? = 'all')";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserItemDto(
                rs.getLong("itemId"),
                rs.getString("itemName"),
                rs.getString("description"),
                rs.getInt("gold"),
                rs.getString("category"),
                rs.getInt("growthPoint"),
                rs.getInt("growthLevel"),
                rs.getBoolean("isSelected")
        ), userId, category, category);
    }

    public String getItemCategory(Long userItemId) {
        String sql = "SELECT i.category FROM item i " +
                "JOIN user_item ui ON i.item_id = ui.item_id2 " +
                "WHERE ui.user_item_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, userItemId);
    }

    public void unequipSameCategoryItems(Long userId, String category) {
        String sql = "UPDATE user_item ui " +
                "JOIN item i ON ui.item_id2 = i.item_id " +
                "SET ui.is_selected = false " +
                "WHERE ui.user_id = ? AND i.category = ?";
        jdbcTemplate.update(sql, userId, category);
    }

    public void updateItemEquippedStatus(Long userId, Long userItemId, boolean isEquipped) {
        String sql = "UPDATE user_item SET is_selected = ? WHERE user_id = ? AND user_item_id = ?";
        jdbcTemplate.update(sql, isEquipped, userId, userItemId);
    }
}


