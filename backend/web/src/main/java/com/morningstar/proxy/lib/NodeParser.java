package com.morningstar.proxy.lib;

import com.morningstar.exception.ProxyNodeTypeUnsupportedException;

public class NodeParser {
    private static final HandlingStrategy[] handlingStrategies = new HandlingStrategy[]{
            new HttpsStrategy(), new Hysteria2Strategy(), new TrojanStrategy(), new V2rayStrategy()
    };

    public static Node parse(String link){
        HandlingStrategy selectedStrategy = null;

        for(HandlingStrategy handlingStrategy : handlingStrategies) {
            if(link.startsWith(handlingStrategy.getProtocol())) {
                selectedStrategy = handlingStrategy;
                break;
            }
        }
        if(selectedStrategy == null) {
            throw new ProxyNodeTypeUnsupportedException(link.split("://")[0]);
        }

        return selectedStrategy.parse(link);
    }
}
