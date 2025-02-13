package com.road_journey.road_journey.my.service;

import com.road_journey.road_journey.my.dao.UserSettingRepository;
import com.road_journey.road_journey.my.domain.SettingDto;
import com.road_journey.road_journey.my.domain.UserSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final UserSettingRepository userSettingRepository;

    public List<SettingDto> getUserSettings(Long userId) {
        List<UserSetting> userSettings = userSettingRepository.findByUser_UserId(userId);

        return userSettings.stream()
                .map(SettingDto::from)
                .collect(Collectors.toList());
    }

    public boolean toggleUserSetting(Long settingId, Long userId) {
        UserSetting userSetting = userSettingRepository.findByUser_UserIdAndSettingId(userId, settingId)
                .orElseThrow(() -> new RuntimeException("설정을 찾을 수 없습니다."));

        boolean isCurrentlyEnabled = userSetting.getStatus().equals("ENABLED");
        userSetting.setStatus(isCurrentlyEnabled ? "DISABLED" : "ENABLED");

        userSettingRepository.save(userSetting); // 변경 사항 저장
        return !isCurrentlyEnabled; // 변경된 상태 반환

    }
}
