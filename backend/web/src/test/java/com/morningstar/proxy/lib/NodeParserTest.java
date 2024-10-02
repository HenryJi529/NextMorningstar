package com.morningstar.proxy.lib;

import com.morningstar.proxy.lib.node.HttpsNode;
import com.morningstar.proxy.lib.node.ShadowsocksNode;
import com.morningstar.proxy.lib.node.TrojanNode;
import com.morningstar.proxy.lib.node.V2rayNode;
import org.junit.jupiter.api.Test;

public class NodeParserTest {
    @Test
    public void testV2ray() {
        String link = "vmess://eyJ2IjogIjIiLCAicHMiOiAiXHU1NDA5XHU2Nzk3XHU3NzAxIFx1NzlmYlx1NTJhOChcdTUxNjhcdTc3MDFcdTkwMWFcdTc1MjgpIiwgImFkZCI6ICIxMTEuMjYuMTA5Ljc5IiwgInBvcnQiOiAiMzA4MjgiLCAiYWlkIjogMiwgInNjeSI6ICJhdXRvIiwgIm5ldCI6ICJ3cyIsICJ0eXBlIjogIm5vbmUiLCAidGxzIjogIiIsICJpZCI6ICJjYmIzZjg3Ny1kMWZiLTM0NGMtODdhOS1kMTUzYmZmZDU0ODQiLCAic25pIjogIiIsICJob3N0IjogIm9jYmMuY29tIiwgInBhdGgiOiAiL29vb28ifQ==";
        Node node = NodeParser.parse(link);
        assert node instanceof V2rayNode;
    }

    @Test
    public void testHttps() {
        String link = "https://MWNhMjY5ZjYtNjYxYi0xMWVmLThjMTEtZjIzYzkxNjRjYTVkOjFjYTI2OWY2LTY2MWItMTFlZi04YzExLWYyM2M5MTY0Y2E1ZEA1MzNlYjg2ZC1zeXhvZzAtdGgwYXZzLTFubXBkLnN2LnBsZWJhaS5uZXQ6NDQzLyPnvo7lnIvopb/pm4XlnJY=";
        Node node = NodeParser.parse(link);
        assert node instanceof HttpsNode;
        assert node.format().equals(link);
    }

    @Test
    public void testShadowsocks() {
        String link = "ss://YWVzLTI1Ni1nY206ZHd6MUd0Rjc=@120.233.128.98:30015#%E5%B9%BF%E4%B8%9C%E7%9C%81+%E7%A7%BB%E5%8A%A8";
        Node node = NodeParser.parse(link);
        assert node instanceof ShadowsocksNode;
        assert node.format().equals(link);
    }

    @Test
    public void testTrojan() {
        String link = "trojan://1ca269f6-661b-11ef-8c11-f23c9164ca5d@dff7810f-syxog0-th0avs-1nmpd.la3.tcpbbr.net:443#美國洛杉磯Torjan";
        Node node = NodeParser.parse(link);
        assert node instanceof TrojanNode;
        assert node.format().equals(link);
    }
}
