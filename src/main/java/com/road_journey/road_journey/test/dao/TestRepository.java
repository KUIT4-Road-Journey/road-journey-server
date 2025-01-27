package com.road_journey.road_journey.test.dao;

import org.springframework.stereotype.Repository;

@Repository
public class TestRepository {

    public String getMessage(String name) {
        return "Hello " + name + "!";
    }
}
