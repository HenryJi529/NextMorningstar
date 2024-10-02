package com.morningstar.proxy.lib;

import com.morningstar.exception.ProxyNodeInvalidException;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpsStrategy implements HandlingStrategy {
    @Override
    public Node parse(String link) {
        String config = new String(Base64.getDecoder().decode(link.substring((this.getProtocolPrefix()).length()).getBytes()));

        String regex = "^([^:]+):([^@]+)@([^:]+):(\\d+)([^#]+)#(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(config);

        if (matcher.find()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            String host = matcher.group(3);
            int port = Integer.parseInt(matcher.group(4));
            String path = matcher.group(5);
            String remark = matcher.group(6);

            return HttpsNode.builder().username(username).password(password).host(host).port(port).path(path).remark(remark).build();
        } else {
            throw new ProxyNodeInvalidException(link);
        }
    }

    @Override
    public String getProtocolPrefix() {
        return "https://";
    }
}
