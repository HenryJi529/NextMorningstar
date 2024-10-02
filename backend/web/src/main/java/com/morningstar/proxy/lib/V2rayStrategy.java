package com.morningstar.proxy.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

public class V2rayStrategy implements HandlingStrategy {
    @Override
    public Node parse(String link) {
        try {
            String config = new String(Base64.getDecoder().decode(link.substring((getProtocol()+"://").length())));
            return new ObjectMapper().readValue(config, V2rayNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getProtocol() {
        return "vmess";
    }
}
