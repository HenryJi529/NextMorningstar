package com.morningstar.proxy.lib;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class V2rayNode implements Node {
    private String ps;
    private String tls;
    private Integer aid;
    private String scy;
    private Integer v;
    private Integer port;
    private String add;
    private String id;
    private String path;
    private String sni;
    private String type;
    private String net;
    private String host;

    @Override
    public String getName() {
        return ps;
    }

    @Override
    public void setName(String newName) {
        this.ps = newName;
    }

    @Override
    public String format() {
        String json;
        try {
            json = JsonUtil.objectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置序列化出错");
        }
        return String.format("%s%s", new V2rayStrategy().getProtocolPrefix(), Base64.getEncoder().encodeToString(json.getBytes()));
    }

    @Override
    public String getProtocol() {
        return "V2ray";
    }
}
