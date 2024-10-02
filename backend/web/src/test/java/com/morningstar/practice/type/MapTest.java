package com.morningstar.practice.type;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    private final Map<String, Integer> map = new HashMap<>();

    @BeforeEach
    public void setUp() {
        map.put("key1", 1);
        map.put("key2", 2);
        map.put("key3", 3);
    }

    @Test
    public void testForEach() {
        map.forEach((k, v) -> System.out.println(k + "=" + v));
    }
}
