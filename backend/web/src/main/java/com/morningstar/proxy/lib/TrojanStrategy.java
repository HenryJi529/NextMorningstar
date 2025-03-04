package com.morningstar.proxy.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrojanStrategy implements HandlingStrategy{
    @Override
    public Node parse(String link) {
        String regex = "^(\\w+)://([^@]+)@([^:]+):(\\d+)#(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);

        if (matcher.find()) {
            String uid = matcher.group(2);
            String host = matcher.group(3);
            int port = Integer.parseInt(matcher.group(4));
            String remark = matcher.group(5);

            return TrojanNode.builder().uid(uid).host(host).port(port).remark(remark).build();
        } else {
            throw new RuntimeException("Invalid link: " + link);
        }
    }

    @Override
    public String getProtocol() {
        return "trojan";
    }
}
