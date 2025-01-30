package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.dto.ShopItemDto;
import com.road_journey.road_journey.items.dto.SpecialItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
@RequiredArgsConstructor
public class ItemShopRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemDto getItemById(long itemId) {
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
                "i.description AS description, i.gold, " +
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

    public int getItemPrice(Long itemId) {
        String sql = "SELECT gold FROM item WHERE item_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, itemId);
    }

    public String getItemCategory(Long itemId) {
        String sql = "SELECT category FROM item WHERE item_id = ?";

        return jdbcTemplate.queryForObject(sql, String.class, itemId);
    }

    public void purchaseItem(Long userId, Long itemId, boolean isCharacter) {
        String sql = "INSERT INTO user_item (user_id, item_id2, is_selected, status, growthPoint, growthlevel) " +
                "VALUES (?, ?, false, 'active', ?, ?)";

        Integer growthPoint = isCharacter ? 0 : null;
        Integer growthLevel = isCharacter ? 0 : null;

        jdbcTemplate.update(sql, userId, itemId, growthPoint, growthLevel);
    }

    public List<SpecialItemDto> findSpecialItems(Long userId) {
        String sql = "SELECT i.item_id AS itemId, i.item_name AS name, i.category, " +
                "i.descriptoin AS description, " +
                "CASE WHEN ui.user_id IS NOT NULL THEN true ELSE false END AS isOwned " +
                "FROM item i " +
                "LEFT JOIN user_item ui ON i.item_id = ui.item_id2 AND ui.user_id = ? " +
                "WHERE i.is_special = true AND i.status = 'active'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new SpecialItemDto(
                rs.getLong("itemId"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getBoolean("isOwned")
        ), userId);
    }

    public Optional<Integer> getRandomSpecialItemId(Long userId) {
        String sql = "SELECT item_id FROM item " +
                "WHERE is_special = true AND status = 'active' " +
                "AND item_id NOT IN (SELECT item_id2 FROM user_item WHERE user_id = ?)";

        List<Integer> specialItemIds = jdbcTemplate.queryForList(sql, Integer.class, userId);

        if (specialItemIds.isEmpty()) {
            System.out.println("사용자 " + userId + " 가 모든 특별 아이템을 보유 중입니다.");
            return Optional.empty();
        }

        return Optional.of(specialItemIds.get(new Random().nextInt(specialItemIds.size())));
    }

    //todo : my 쪽에 있는게 좋지 않나?
    public int getUserGold(Long userId) {
        String sql = "SELECT gold FROM user WHERE user_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    public void updateUserGold(Long userId, int newGold) {
        String sql = "UPDATE user SET gold = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, newGold, userId);
    }
}
