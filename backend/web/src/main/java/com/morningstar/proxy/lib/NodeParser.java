package com.morningstar.proxy.lib;

import com.morningstar.exception.ProxyNodeTypeUnsupportedException;

public class NodeParser {
    private static final HandlingStrategy[] handlingStrategies = new HandlingStrategy[]{
            new HttpsStrategy(), new Hysteria2Strategy(), new TrojanStrategy(), new V2rayStrategy(), new ShadowsocksStrategy()
    };

    public static Node parse(String link) {
        HandlingStrategy selectedStrategy = null;

        for (HandlingStrategy handlingStrategy : handlingStrategies) {
            if (link.startsWith(handlingStrategy.getProtocolPrefix())) {
                selectedStrategy = handlingStrategy;
                break;
            }
        }
        if (selectedStrategy == null) {
            throw new ProxyNodeTypeUnsupportedException(link);
        }

        return selectedStrategy.parse(link);
    }
}
