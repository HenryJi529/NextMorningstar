package com.morningstar.proxy.lib;

import org.junit.jupiter.api.Test;

public class ShadowsocksTest {
    @Test
    public void test() {
        String link = "ss://YWVzLTI1Ni1nY206ZHd6MUd0Rjc=@120.233.128.98:30015#%E5%B9%BF%E4%B8%9C%E7%9C%81+%E7%A7%BB%E5%8A%A8";
        ShadowsocksNode node = (ShadowsocksNode) new ShadowsocksStrategy().parse(link);
        System.out.println(node);
        System.out.println(node.format());
        assert link.equals(node.format());
    }
}
