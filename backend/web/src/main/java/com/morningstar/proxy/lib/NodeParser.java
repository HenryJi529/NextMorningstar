package com.morningstar.proxy.lib;

import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.infra.util.ClassUtil;

import java.util.Map;

public class NodeParser {
    private static final Map<Class<? extends HandlingStrategy>, HandlingStrategy> strategyMap = ClassUtil.loadInstancesByPackageName(
            HandlingStrategy.class,
            "com.morningstar.proxy.lib.strategy"
    );

    public static Node parse(String link) {
        HandlingStrategy selectedStrategy = null;

        for (HandlingStrategy handlingStrategy : strategyMap.values()) {
            if (link.startsWith(handlingStrategy.getProtocolPrefix())) {
                selectedStrategy = handlingStrategy;
                break;
            }
        }
        if (selectedStrategy == null) {
            throw new BaseException(ResponseCode.PROXY_NODE_TYPE_UNSUPPORTED, link);
        }

        return selectedStrategy.parse(link);
    }
}
