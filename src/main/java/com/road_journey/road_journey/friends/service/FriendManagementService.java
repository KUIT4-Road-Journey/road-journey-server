package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.entity.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendManagementService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    //친구 목록 조회
    public List<FriendListDTO> getFriends(Long userId, String sortBy) {
        List<Friend> friends = friendRepository.findFriendsByUserId(userId);

        if (friends.isEmpty()) {
            return Collections.emptyList();
        }

        List<FriendListDTO> friendList = friends.stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getFriendUserId())
                            .orElse(null);

                    long lastLoginMillis = Optional.ofNullable(user)
                            .map(User::getLastLoginTime)
                            .map(time -> time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .orElse(0L);

                    return new FriendListDTO(user, lastLoginMillis, getAchievementCount(user.getUserId()));
                })
                .collect(Collectors.toList());

        return sortFriendsByCategory(friendList, sortBy);
    }

    private List<FriendListDTO> sortFriendsByCategory(List<FriendListDTO> friends, String sortBy) {
        switch (sortBy) {
            case "lastLogin":
                friends.sort(Comparator.comparing(FriendListDTO::getLastLoginTime, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "goals":
                friends.sort(Comparator.comparingInt(FriendListDTO::getAchievementCount).reversed());
                break;
            case "alphabetical":
            default:
                friends.sort(Comparator.comparing(FriendListDTO::getNickname));
                break;
        }
        return friends;
    }

    private int getAchievementCount(Long userId) {
        // todo : 목표 테이블에서 해당 유저의 달성 목표 수 조회
        return 0;
    }

    public Optional<Object> getFriendMain() {
        //todo : 개별 친구 프로필 접근 (main 쪽 완성되면 땡겨서 사용하면 될듯?)
        return Optional.empty();
    }

    //친구 좋아요 상태 변경
    @Transactional
    public UpdateResponseDTO updateFriendLike(Long friendId, Boolean isLike) {
        Optional<Friend> friendOptional = friendRepository.findFriendsByfriendUserId(friendId);

        if (friendOptional.isEmpty()) {
            return new UpdateResponseDTO("failed", "Friend not found.");
        }

        Friend friend = friendOptional.get();
        friend.setIsLike(isLike);
        friendRepository.save(friend);

        return new UpdateResponseDTO("success", "Friend like status updated.");
    }
}
