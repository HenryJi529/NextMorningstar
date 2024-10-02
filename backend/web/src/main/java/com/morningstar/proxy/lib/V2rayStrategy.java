package com.morningstar.proxy.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.exception.ProxyNodeInvalidException;
import com.morningstar.util.JsonUtil;

import java.util.Base64;

public class V2rayStrategy implements HandlingStrategy {
    @Override
    public Node parse(String link) {
        try {
            String config = new String(Base64.getDecoder().decode(link.substring((getProtocolPrefix()).length())));
            return JsonUtil.objectMapper().readValue(config, V2rayNode.class);
        } catch (JsonProcessingException e) {
            throw new ProxyNodeInvalidException(link);
        }
    }

    @Override
    public String getProtocolPrefix() {
        return "vmess://";
    }
}
