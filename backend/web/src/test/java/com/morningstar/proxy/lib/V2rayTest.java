package com.morningstar.proxy.lib;

import org.junit.jupiter.api.Test;

public class V2rayTest {
    @Test
    public void test() {
        String link = "vmess://eyJ2IjogIjIiLCAicHMiOiAiXHU1NDA5XHU2Nzk3XHU3NzAxIFx1NzlmYlx1NTJhOChcdTUxNjhcdTc3MDFcdTkwMWFcdTc1MjgpIiwgImFkZCI6ICIxMTEuMjYuMTA5Ljc5IiwgInBvcnQiOiAiMzA4MjgiLCAiYWlkIjogMiwgInNjeSI6ICJhdXRvIiwgIm5ldCI6ICJ3cyIsICJ0eXBlIjogIm5vbmUiLCAidGxzIjogIiIsICJpZCI6ICJjYmIzZjg3Ny1kMWZiLTM0NGMtODdhOS1kMTUzYmZmZDU0ODQiLCAic25pIjogIiIsICJob3N0IjogIm9jYmMuY29tIiwgInBhdGgiOiAiL29vb28ifQ==";
        V2rayNode node = (V2rayNode) new V2rayStrategy().parse(link);
        System.out.println(node);
        System.out.println(node.format());
    }
}