package com.morningstar.exception;

public class ProxyNodeInvalidException extends BaseException {
    public ProxyNodeInvalidException(String link) {
        super("代理节点不合法: " + link);
    }
}
