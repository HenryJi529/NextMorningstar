package com.morningstar.proxy.lib;

import com.morningstar.exception.ProxyNodeInvalidException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrojanStrategy implements HandlingStrategy {
    @Override
    public Node parse(String link) {
        String regex = "^(\\w+)://([^@]+)@([^:]+):(\\d+)\\??([^#]*)?#(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);

        if (matcher.find()) {
            String uid = matcher.group(2);
            String host = matcher.group(3);
            int port = Integer.parseInt(matcher.group(4));
            String queries = matcher.group(5);
            String remark = matcher.group(6);
            if (remark.startsWith("%")) {
                try {
                    remark = new URI(remark).getPath();
                } catch (URISyntaxException ignore) {
                }
            }
            return TrojanNode.builder().uid(uid).host(host).port(port).queries(queries).remark(remark).build();
        } else {
            throw new ProxyNodeInvalidException(link);
        }
    }

    @Override
    public String getProtocolPrefix() {
        return "trojan://";
    }
}
