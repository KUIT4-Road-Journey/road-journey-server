package com.road_journey.road_journey.my.dao;

import com.road_journey.road_journey.my.domain.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    // 특정 사용자의 모든 설정 가져오기
    List<UserSetting> findByUser_UserId(Long userId);

    // 특정 사용자의 특정 설정 가져오기
    Optional<UserSetting> findByUser_UserIdAndSettingId(Long userId, Long settingId);
}
