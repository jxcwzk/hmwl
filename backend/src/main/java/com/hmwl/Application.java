package com.hmwl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@SpringBootApplication
@MapperScan("com.hmwl.mapper")
@RestController
public class Application {
    private static ApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
    }

    @GetMapping("/debug/controllers")
    public Map<String, Object> debugControllers() {
        Map<String, Object> result = new HashMap<>();
        String[] beanNames = context.getBeanNamesForAnnotation(RestController.class);
        result.put("restControllers", Arrays.asList(beanNames));
        result.put("totalCount", beanNames.length);
        return result;
    }

    @GetMapping("/test/dispatch")
    public Map<String, String> testDispatch() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "test from Application");
        return result;
    }
}
