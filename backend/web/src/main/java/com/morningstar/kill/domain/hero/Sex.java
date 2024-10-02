package com.morningstar.kill.domain.hero;

public enum Sex {
    MALE("男"), FEMALE("女");
    private final String value;
    Sex(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
