package com.morningstar.exception;

public class ProxyNodeTypeUnsupportedException extends BaseException {
    public ProxyNodeTypeUnsupportedException(String prefix) {
        super("代理节点类型不支持: " + prefix);
    }
}
