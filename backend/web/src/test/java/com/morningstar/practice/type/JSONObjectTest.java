package com.morningstar.practice.type;

import cn.hutool.json.JSONObject;
import org.junit.jupiter.api.Test;

public class JSONObjectTest {
    @Test
    public void testBuildAndFormat() {
        JSONObject json = new JSONObject();
        json.set("name", "tom");
        json.set("age", 18);
        System.out.println(json);
    }

    @Test
    public void testParse() {
        String jsonString = "{\"name\":\"tom\",\"age\":18}";
        JSONObject jsonObject = new JSONObject(jsonString);
        System.out.println(jsonObject);
    }

}
