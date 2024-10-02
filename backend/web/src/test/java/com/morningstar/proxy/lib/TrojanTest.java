package com.morningstar.proxy.lib;

import org.junit.jupiter.api.Test;

public class TrojanTest {
    @Test
    public void test1() {
        String link = "trojan://telegram-id-privatevpns@35.156.229.197:22222?sni=trojan.burgerip.co.uk#%E5%BE%B7%E5%9B%BD+%E9%BB%91%E6%A3%AE%E5%B7%9E%E6%B3%95%E5%85%B0%E5%85%8B%E7%A6%8FAmazon%E6%95%B0%E6%8D%AE%E4%B8%AD%E5%BF%83";
        TrojanNode node = (TrojanNode) new TrojanStrategy().parse(link);
        System.out.println(node);
        System.out.println(node.format());
    }

    @Test
    public void test2() {
        String link = "trojan://1ca269f6-661b-11ef-8c11-f23c9164ca5d@dff7810f-syxog0-th0avs-1nmpd.la3.tcpbbr.net:443#美國洛杉磯Torjan";
        TrojanNode node = (TrojanNode) new TrojanStrategy().parse(link);
        System.out.println(node);
        System.out.println(node.format());
    }

    @Test
    public void test3() {
        String link = "trojan://d6b8011a-c725-435a-9fec-bf6d3530392c@104.17.128.1:2096?sni=vle.amclubdns.dpdns.org&allowInsecure=1&type=ws&path=%2F%3Fed%3D2560%26PROT_TYPE%3Dtrojan&host=vle.amclubdns.dpdns.org&fp=chrome#%E7%BE%8E%E5%9B%BD+CloudFlare%E8%8A%82%E7%82%B9";
        TrojanNode node = (TrojanNode) new TrojanStrategy().parse(link);
        System.out.println(node);
        System.out.println(node.format());
    }
}
