package com.morningstar.proxy.lib;

import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;

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
            throw new BaseException(ResponseCode.PROXY_LINK_UNSUPPORTED);
        }

        return selectedStrategy.parse(link);
    }
}
