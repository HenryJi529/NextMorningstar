package com.morningstar.kill.exception;

public class SharedTableCreatedWithDifferentMemberCountsException extends RuntimeException {
    public SharedTableCreatedWithDifferentMemberCountsException() {
        super("使用了不同人数的Room创建SharedTable");
    }
}
