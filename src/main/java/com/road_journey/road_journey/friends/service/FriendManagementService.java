package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserDTO;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
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
    public List<FriendListDTO> getFriends(Long userId, String category) {
        List<Friend> friends = friendRepository.findFriendsByUserId(0);

        List<FriendListDTO> friendList = friends.stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getFriendUserId())
                            .orElseThrow(() -> new UserNotFoundException("User not found"));
                    int friendStatus = "active".equals(friend.getStatus()) ? 2 : 1; // 2: 친구, 1: 대기중

                    // LocalDateTime을 밀리초로 변환
                    long lastLoginMillis = Optional.ofNullable(user.getLastLoginTime())
                            .map(time -> time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .orElse(0L);

                    return new FriendListDTO(user, friendStatus, lastLoginMillis, getAchievementCount(user.getUserId()));
                })
                .collect(Collectors.toList());

        return sortFriendsByCategory(friendList, category);
    }

    private List<FriendListDTO> sortFriendsByCategory(List<FriendListDTO> friends, String category) {
        switch (category) {
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

    //todo : 개별 친구 프로필 접근 (main 쪽 완성되면 땡겨서 사용하면 될듯?)
    public Optional<Object> getFriendMain() {
        return Optional.empty();
    }

    //친구 좋아요 상태 변경
    @Transactional
    public UpdateResponseDTO updateFriendLike(Long userId, Long friendId, Boolean isLike) {
        Friend friend = friendRepository.findActiveFriend(userId, friendId)
                .orElseThrow(() -> new FriendNotFoundException("Friend not found"));

        friend.setIsLike(isLike);
        friendRepository.save(friend);

        return new UpdateResponseDTO("success", "Friend like status updated.");
    }
}
