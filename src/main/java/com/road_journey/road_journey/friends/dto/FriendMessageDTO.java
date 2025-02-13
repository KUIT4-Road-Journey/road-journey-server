package com.road_journey.road_journey.friends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendMessageDTO {
    private Long messageId;
    private String profilePicUrl;
    private String message;
    private String createdAt;
    private String category;
    private Object detail;  // 공유 목표 상세 정보 or FriendUserId
}