package com.morningstar.proxy.lib;

public interface HandlingStrategy {
    Node parse(String link);

    String getProtocolPrefix();
}
