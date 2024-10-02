package com.morningstar.proxy.lib;

import lombok.Getter;

@Getter
public enum Country {
    HK("香港", "香港"),
    TW("台灣,台湾", "台湾"),
    US("美國,美国,硅谷,洛杉磯,洛杉矶", "美国"),
    JP("日本", "日本"),
    KR("韓,韩", "韩国"),
    UK("英國,英国", "英国"),
    DE("德國,德国", "德国"),
    FR("法国", "法国"),
    ID("印尼", "印尼"),
    RU("俄羅斯,俄罗斯", "俄罗斯"),
    SG("新加坡", "新加坡"),
    CH("瑞士", "瑞士"),
    MY("马来西亚", "马来");

    private final String aliases;
    private final String name;

    Country(String aliases, String name) {
        this.aliases = aliases;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name();
    }
}