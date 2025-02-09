package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.road_journey.road_journey.friends.dto.FriendStatus.IS_FRIEND;
import static com.road_journey.road_journey.friends.dto.FriendStatus.IS_NOT_FRIEND;

@Service
@RequiredArgsConstructor
public class FriendSearchService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public List<FriendDTO> searchUsers(Long userId, String searchId) {
        List<User> users = userRepository.findUsersByAccountId(searchId);

        return users.stream()
                .filter(user -> !user.getUserId().equals(userId))
                .map(user -> {
                    Optional<Friend> optionalFriend = friendRepository.findFriendByUserIdAndFriendUserId(userId, user.getUserId());

                    String friendStatus = IS_NOT_FRIEND.name();
                    Long friendId = null;

                    if (optionalFriend.isPresent()) {
                        Friend friend = optionalFriend.get();
                        friendStatus = friend.getStatus();
                        friendId = friend.getFriendId();
                    }

                    return IS_FRIEND.name().equals(friendStatus) ? null : new FriendDTO(user, friendStatus, friendId);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
