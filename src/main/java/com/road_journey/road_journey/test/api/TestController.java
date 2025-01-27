package com.road_journey.road_journey.test.api;

import com.road_journey.road_journey.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Test")
@RestController
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/hello/{name}")
    @Operation(summary = "서버 응답 테스트 용", description = "이름 입력 시 인사 반환")
    public Map<String, Object> sayHello(@PathVariable String name) {
        String message = testService.getHelloMessage(name);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("isSuccess", true);
        Map<String, String> responseData = new LinkedHashMap<>();
        responseData.put("message", message);
        response.put("response", responseData);
        return response;
    }

}
