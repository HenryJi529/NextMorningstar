package com.morningstar.kill.player;

public enum Identity {
    Lord("主公"), Rebel("反贼"), Loyal("忠臣"), Traitor("内奸"),
    WEI("魏"), SHU("蜀"), WU("吴"), QUN("群"), YE("野");

    private final String value;

    Identity(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
