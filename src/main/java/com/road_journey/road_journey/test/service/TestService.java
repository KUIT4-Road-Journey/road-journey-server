package com.road_journey.road_journey.test.service;

import com.road_journey.road_journey.test.dao.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public String getHelloMessage(String name) {
        return testRepository.getMessage(name);
    }

}
