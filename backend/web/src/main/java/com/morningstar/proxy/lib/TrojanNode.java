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
    private String queries;

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
        if (queries.isEmpty()) {
            return String.format("%s%s@%s:%d#%s", new TrojanStrategy().getProtocolPrefix(), uid, host, port, remark);
        } else {
            return String.format("%s%s@%s:%d?%s#%s", new TrojanStrategy().getProtocolPrefix(), uid, host, port, queries, remark);
        }
    }

    @Override
    public String getProtocol() {
        return "Trojan";
    }
}