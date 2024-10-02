package com.morningstar.proxy.lib;

import com.morningstar.exception.ProxyNodeInvalidException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShadowsocksStrategy implements HandlingStrategy {
    @Override
    public Node parse(String link) {
        String regex = getProtocolPrefix() + "([^:]+)@([^:]+):(\\d+)#(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
            String uid = matcher.group(1);
            String host = matcher.group(2);
            Integer port = Integer.parseInt(matcher.group(3));
            String remark;
            try {
                remark = new URI(matcher.group(4).replace("+", "%20")).getPath();
            } catch (URISyntaxException e) {
                throw new ProxyNodeInvalidException(link);
            }
            return ShadowsocksNode.builder().uid(uid).host(host).port(port).remark(remark).build();
        } else {
            throw new ProxyNodeInvalidException(link);
        }
    }

    @Override
    public String getProtocolPrefix() {
        return "ss://";
    }
}
