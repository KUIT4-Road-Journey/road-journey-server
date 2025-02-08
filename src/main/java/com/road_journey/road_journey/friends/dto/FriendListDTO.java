package com.road_journey.road_journey.friends.dto;

import com.road_journey.road_journey.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendListDTO {
    private Long userId;
    private String accountId;
    private String nickname;
    private String profileImage;
    private String statusMessage;
    private Long lastLoginTime;
    private int achievementCount;

    public FriendListDTO(User user, Long lastLoginTime, int achievementCount) {
        this.userId = user.getUserId();
        this.accountId = user.getAccountId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.statusMessage = user.getStatusMessage();
        this.lastLoginTime = lastLoginTime;
        this.achievementCount = achievementCount;
    }
}
