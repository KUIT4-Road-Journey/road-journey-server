package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserDTO;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendSearchService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    //검색 결과에서 `status = 'active'`인 유저만 반환
    public List<UserDTO> searchUsers(Long userId, String searchId, String scope) {
        List<User> users = userRepository.findUsersBySearchId(searchId, scope);

        return users.stream()
                .map(user -> {
                    // 친구 상태 확인
                    Friend friend = friendRepository.findByUserIdAndFriendUserId(userId, user.getUserId()).orElse(null);

                    int friendStatus = 0; // 기본값: 친구 아님
                    if (friend != null) {
                        if ("active".equals(friend.getStatus())) {
                            friendStatus = 2; // 친구 관계
                        } else if ("대기중".equals(friend.getStatus())) {
                            friendStatus = 1; // 요청 대기중
                        }
                    }

                    return new UserDTO(user, friendStatus);
                })
                .collect(Collectors.toList());
    }
}
