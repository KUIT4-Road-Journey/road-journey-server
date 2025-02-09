package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.road_journey.road_journey.friends.dto.FriendStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@TestPropertySource(locations = "classpath:application-test.properties")
public class FriendSearchServiceTest {

    @Autowired
    private FriendSearchService friendSearchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    private Long userId1;
    private Long userId2;
    private Long userId3;
    private Long friendId;

    @BeforeEach
    public void setUp() {
        User user1 = new User("user1", "password1", "user1@test.com", "User One", 0L, "active");
        User user2 = new User("user2", "password2", "user2@test.com", "User Two", 0L, "active");
        User user3 = new User("user3", "password3", "user3@test.com", "User Three", 0L, "active");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();
        userId3 = user3.getUserId();


        friendId = friendRepository.save(new Friend(userId1, userId2, false, PENDING.name())).getFriendId();
    }

    @Test
    public void accountId_일부_검색_친구_대기중_확인() {
        List<FriendDTO> result = friendSearchService.searchUsers(userId1, "user2");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(friendId, result.get(0).getFriendId());
        assertEquals("user2", result.get(0).getAccountId());
        assertEquals(userId2, result.get(0).getUserId());
        assertEquals(PENDING.name(), result.get(0).getFriendStatus());
    }

    @Test
    public void accountId_일부_검색_친구_아닌_사용자_확인() {
        List<FriendDTO> result = friendSearchService.searchUsers(userId1, "user3");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNull(result.get(0).getFriendId());
        assertEquals("user3", result.get(0).getAccountId());
        assertEquals(userId3, result.get(0).getUserId());
        assertEquals(IS_NOT_FRIEND.name(), result.get(0).getFriendStatus());
    }

    @Test
    public void 존재하지_않는_accountId로_검색() {
        List<FriendDTO> result = friendSearchService.searchUsers(userId1, "nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    public void 여러_accountId_검색_결과_자기자신_제외_확인() {
        List<FriendDTO> result = friendSearchService.searchUsers(userId1, "user");

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(dto -> dto.getUserId().equals(userId1)));
    }
}