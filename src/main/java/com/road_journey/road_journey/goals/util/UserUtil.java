package com.road_journey.road_journey.goals.util;

import com.road_journey.road_journey.friends.entity.Friend;

import java.util.List;
import java.util.Objects;

public class UserUtil {

    public UserUtil() {

    }

    public static boolean isMe(Long myUserId, Long userId) {
        return Objects.equals(myUserId, userId);
    }

    public static boolean isMyFriend(Long myUserId, Long userId, List<Friend> friendList) {
        for (Friend friend : friendList) {
            if (Objects.equals(friend.getFriendUserId(), userId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMeOrMyFriend(Long myUserId, Long userId, List<Friend> friendList) {
        return isMe(myUserId, userId) || isMyFriend(myUserId, userId, friendList);
    }
}
