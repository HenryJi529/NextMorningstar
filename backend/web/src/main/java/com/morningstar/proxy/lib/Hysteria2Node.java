package com.morningstar.proxy.lib;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hysteria2Node implements Node {
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
        return new Hysteria2Strategy().getProtocol();
    }
}
