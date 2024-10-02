package com.morningstar.proxy.lib;

import lombok.Getter;

@Getter
public enum Country {
    HK(null, "香港"),
    TW("台灣", "台湾"),
    US("美國", "美国"),
    JP(null, "日本"),
    KR("韓", "韩"),
    UK("英國", "英国"),
    DE("德國", "德国"),
    FR(null, "法国"),
    ID(null, "印尼"),
    RU("俄羅斯", "俄罗斯"),
    SG(null, "新加坡")
    ;

    private final String TraditionalCN;
    private final String SimplifiedCN;

    Country(String TraditionalCN, String SimplifiedCN) {
        this.TraditionalCN = TraditionalCN;
        this.SimplifiedCN = SimplifiedCN;
    }

    @Override
    public String toString() {
        return this.name();
    }
}