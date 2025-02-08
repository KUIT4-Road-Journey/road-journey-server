package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.friends.dto.FriendStatus;
import com.road_journey.road_journey.friends.dto.FriendUserDTO;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendSearchService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public List<FriendUserDTO> searchUsers(Long userId, String searchId) {
        List<User> users = userRepository.findUsersByAccountId(searchId);

        return users.stream()
                .map(user -> {
                    Optional<Friend> optionalFriend = friendRepository.findFriendByUserIdAndFriendUserId(userId, user.getUserId());

                    String friendStatus = "isNotFriend";  // isNotFriend, pending, isFriend
                    Long friendId = null;

                    if (optionalFriend.isPresent()) {
                        Friend friend = optionalFriend.get();
                        friendStatus = friend.getStatus();
                        friendId = friend.getFriendId();
                    }

                    return "isFriend".equals(friendStatus) ? null : new FriendUserDTO(user, FriendStatus.PENDING, friendId);
                })
                .filter(Objects::nonNull) // 친구 관계는 제외
                .collect(Collectors.toList());
    }
}
