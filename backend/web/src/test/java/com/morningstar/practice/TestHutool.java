package com.morningstar.practice;

import cn.hutool.json.JSONObject;
import org.junit.jupiter.api.Test;

public class TestHutool {
    @Test
    public void testJsonUtil() {
        JSONObject json = new JSONObject();
        json.set("name", "tom");
        json.set("age", 18);
        System.out.println(json);
    }
}
