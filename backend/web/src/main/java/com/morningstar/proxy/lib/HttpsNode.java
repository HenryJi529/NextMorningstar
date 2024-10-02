package com.morningstar.proxy.lib;

import lombok.Builder;
import lombok.Data;

import java.util.Base64;

@Data
@Builder
public class HttpsNode implements Node {
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String path;
    private String remark;

    @Override
    public String getName() {
        return remark;
    }

    @Override
    public void setName(String newName) {
        this.remark = newName;
    }

    @Override
    public String format() {
        String config = String.format("%s:%s@%s:%d%s#%s", username, password, host, port, path, remark);
        return String.format("%s%s", new HttpsStrategy().getProtocolPrefix(), Base64.getEncoder().encodeToString(config.getBytes()));
    }

    @Override
    public String getProtocol() {
        return "HTTPS";
    }
}