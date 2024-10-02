package com.morningstar.proxy.lib;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TrojanNode implements Node {
    private String uid;
    private String host;
    private Integer port;
    private String remark;

    @Override
    public void setName(String newName) {
        this.remark = newName;
    }

    @Override
    public String getName() {
        return remark;
    }

    @Override
    public String format() {
        return String.format("%s://%s@%s:%d#%s", getProtocol(), uid, host, port, remark);
    }

    @Override
    public String getProtocol() {
        return new TrojanStrategy().getProtocol();
    }
}