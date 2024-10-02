package com.morningstar.pic.lib;

public class RelayImageLinkGenerator implements ImageLinkGenerator {
    @Override
    public String generate(String ownerName, String filePath) {
        return String.format("https://morningstar369.com/api/pic/resource/relay/%s/%s", ownerName, filePath);
    }
}
