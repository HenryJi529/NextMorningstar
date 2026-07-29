package com.morningstar.dev.statemachine;

import lombok.Getter;

@Getter
public enum Event {
    START("触发容器启动"),
    START_SUCCEEDED("容器启动成功"),
    START_FAILED("容器启动失败"),

    SYNC("触发仓库同步"),
    SYNC_SUCCEEDED("仓库同步成功"),
    SYNC_FAILED("仓库同步失败"),

    SCAN("触发漏洞扫描"),
    SCAN_SUCCEEDED("漏洞扫描成功"),
    SCAN_FAILED("漏洞扫描失败"),

    FIX("触发漏洞修复"),
    FIX_SUCCEEDED("漏洞修复成功"),
    FIX_FAILED("漏洞修复失败"),

    VERIFY("触发验证"),
    VERIFY_SUCCEEDED("验证成功"),
    VERIFY_FAILED("验证失败"),

    SUBMIT("触发提交"),
    SUBMIT_SUCCEEDED("提交成功"),
    SUBMIT_FAILED("提交失败"),

    CLEAN("触发清理"),
    CLEAN_SUCCEEDED("清理成功"),
    CLEAN_FAILED("清理失败"),

    RESTORE("触发还原"),
    RESTORE_SUCCEEDED("还原成功"),
    RESTORE_FAILED("还原失败"),
    ;

    private final String name;

    Event(String name) {
        this.name = name;
    }
}
