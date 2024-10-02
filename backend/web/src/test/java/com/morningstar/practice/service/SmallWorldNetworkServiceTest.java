package com.morningstar.practice.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmallWorldNetworkServiceTest {
    @Resource
    private SmallWorldNetworkService smallWorldNetworkService;

    @Test
    public void testFindByNameContainingIgnoreCase() {
        System.out.println(smallWorldNetworkService.findByNameContainingIgnoreCase("Tom H"));
    }
}
