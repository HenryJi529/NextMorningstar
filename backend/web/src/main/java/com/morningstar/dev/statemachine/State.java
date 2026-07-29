package com.morningstar.dev.statemachine;

import lombok.Getter;

@Getter
public enum State {
    PENDING("待执行"),

    STARTING("容器启动中"),
    STARTED("容器启动完成"),

    SYNCING("仓库同步中"),
    SYNCED("仓库同步完成"),

    SCANNING("漏洞扫描中"),
    SCANNED("漏洞扫描完成"),

    FIXING("漏洞修复中"),
    FIXED("漏洞修复完成"),

    VERIFYING("修复验证中"),
    VERIFIED("修复验证完成"),

    SUBMITTING("PR提交中"),
    SUBMITTED("PR提交完成"),

    CLEANING("容器清理中"),
    CLEANED("容器清理完成"),

    RESTORING("代码还原中"),
    RESTORED("代码还原完成"),

    FAILED("执行失败"),
    ;

    private final String name;

    State(String name) {
        this.name = name;
    }
}
