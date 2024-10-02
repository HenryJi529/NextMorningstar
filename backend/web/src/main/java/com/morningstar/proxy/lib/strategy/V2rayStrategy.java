package com.morningstar.proxy.lib.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.infra.util.JsonUtil;
import com.morningstar.proxy.lib.HandlingStrategy;
import com.morningstar.proxy.lib.Node;
import com.morningstar.proxy.lib.node.V2rayNode;

import java.util.Base64;

public class V2rayStrategy implements HandlingStrategy {
    @Override
    public Node parse(String link) {
        try {
            String config = new String(Base64.getDecoder().decode(link.substring((getProtocolPrefix()).length())));
            return JsonUtil.objectMapper().readValue(config, V2rayNode.class);
        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseCode.PROXY_NODE_CONTENT_INVALID, link);
        }
    }

    @Override
    public String getProtocolPrefix() {
        return "vmess://";
    }
}
