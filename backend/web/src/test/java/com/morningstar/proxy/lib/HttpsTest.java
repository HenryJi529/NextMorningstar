package com.morningstar.proxy.lib;

import org.junit.jupiter.api.Test;

public class HttpsTest {
    @Test
    public void test() {
        String link = "https://MWNhMjY5ZjYtNjYxYi0xMWVmLThjMTEtZjIzYzkxNjRjYTVkOjFjYTI2OWY2LTY2MWItMTFlZi04YzExLWYyM2M5MTY0Y2E1ZEA1MzNlYjg2ZC1zeXhvZzAtdGgwYXZzLTFubXBkLnN2LnBsZWJhaS5uZXQ6NDQzLyPnvo7lnIvopb/pm4XlnJY=";
        HttpsNode node = (HttpsNode) new HttpsStrategy().parse(link);
        System.out.println(node);
        System.out.println(node.format());
    }
}