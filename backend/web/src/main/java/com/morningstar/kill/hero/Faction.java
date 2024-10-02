package com.morningstar.kill.hero;

public enum Faction {
    WEI("魏"), SHU("蜀"), WU("吴"), QUN("群");

    private final String value;

    Faction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
