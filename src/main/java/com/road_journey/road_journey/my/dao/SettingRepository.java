package com.road_journey.road_journey.my.dao;

import com.road_journey.road_journey.my.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {
}