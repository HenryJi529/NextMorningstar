package com.morningstar.proxy.lib.node;

import com.morningstar.proxy.lib.Node;
import com.morningstar.proxy.lib.strategy.ShadowsocksStrategy;
import lombok.Builder;
import lombok.Data;
import org.yaml.snakeyaml.util.UriEncoder;

@Data
@Builder
public class ShadowsocksNode implements Node {
    private String uid;
    private String host;
    private Integer port;
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
        return String.format("%s%s@%s:%d#%s", new ShadowsocksStrategy().getProtocolPrefix(), uid, host, port, UriEncoder.encode(remark).replace("%20", "+"));
    }

    @Override
    public String getProtocol() {
        return "Shadowsocks";
    }
}
