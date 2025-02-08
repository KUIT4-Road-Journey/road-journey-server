package com.road_journey.road_journey.friends.dto;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.friends.entity.Friend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendUserDTO {
    private Long userId;
    private String accountId;
    private String nickname;
    private String profileImage;
    private String statusMessage;
    private FriendStatus friendStatus;
    private Long friendId; // 친구 요청 ID

    public FriendUserDTO(User user, FriendStatus friendStatus, Long friendId) {
        this.userId = user.getUserId();
        this.accountId = user.getAccountId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.statusMessage = user.getStatusMessage();

        this.friendStatus = friendStatus;

        this.friendId = friendId;
    }
}

