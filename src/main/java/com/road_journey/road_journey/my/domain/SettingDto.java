package com.road_journey.road_journey.my.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettingDto {
    private Long id;
    private String category;
    private String settingName;
    private boolean isActivated;

    public static SettingDto from(UserSetting userSetting) {
        return new SettingDto(
                userSetting.getSetting().getId(),
                userSetting.getSetting().getStatus(),
                userSetting.getSetting().getName(),
                userSetting.getStatus().equals("ENABLED")
        );
    }
}
