package com.road_journey.road_journey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoadJourneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadJourneyApplication.class, args);
	}

}
