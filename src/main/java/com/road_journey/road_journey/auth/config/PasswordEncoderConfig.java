package com.road_journey.road_journey.auth.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    // PasswordEncoder Bean
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // ModelMapper Bean
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
