package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.dto.ShopItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemShopRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemDto getItemById(int itemId) {
        String sql = "SELECT item_id AS itemId, item_name AS itemName, description, gold, category, is_special AS isSpecial, status " +
                "FROM item WHERE item_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ItemDto(
                    rs.getLong("itemId"),
                    rs.getString("itemName"),
                    rs.getString("description"),
                    rs.getInt("gold"),
                    rs.getString("category"),
                    rs.getBoolean("isSpecial"),
                    rs.getString("status")
            ), itemId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public List<ShopItemDto> findShopItemsByCategory(Long userId, String category) {
        String sql = "SELECT i.item_id AS itemId, i.item_name AS itemName, i.category, " +
                "i.descriptoin AS description, i.gold, " +
                "CASE WHEN ui.user_id IS NOT NULL THEN true ELSE false END AS isOwned, " +
                "CASE WHEN ui.is_selected = true THEN true ELSE false END AS isSelected " +
                "FROM item i " +
                "LEFT JOIN user_item ui ON i.item_id = ui.item_id2 AND ui.user_id = ? " +
                "WHERE (i.category = ? OR ? = 'all') AND i.status = 'active'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new ShopItemDto(
                rs.getLong("itemId"),
                rs.getString("itemName"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getInt("gold"),
                rs.getBoolean("isSelected"),
                rs.getBoolean("isOwned")
        ), userId, category, category);
    }

    public int getItemPrice(int itemId) {
        String sql = "SELECT gold FROM item WHERE item_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, itemId);
    }

    public String getItemCategory(int itemId) {
        String sql = "SELECT category FROM item WHERE item_id = ?";

        return jdbcTemplate.queryForObject(sql, String.class, itemId);
    }

    public void purchaseItem(Long userId, int itemId, boolean isCharacter) {
        String sql = "INSERT INTO user_item (user_id, item_id2, is_selected, status, growthPoint, growthlevel) " +
                "VALUES (?, ?, false, 'active', ?, ?)";

        Integer growthPoint = isCharacter ? 0 : null;
        Integer growthLevel = isCharacter ? 0 : null;

        jdbcTemplate.update(sql, userId, itemId, growthPoint, growthLevel);
    }

    //todo : my 쪽에 있는게 좋지 않나? 사용하기 번거롭나?
    public int getUserGold(Long userId) {
        String sql = "SELECT gold FROM user WHERE user_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    public void updateUserGold(Long userId, int newGold) {
        String sql = "UPDATE user SET gold = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, newGold, userId);
    }
}
