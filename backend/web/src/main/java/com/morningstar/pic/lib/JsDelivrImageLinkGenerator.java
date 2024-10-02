package com.morningstar.pic.lib;

public class JsDelivrImageLinkGenerator implements ImageLinkGenerator {
    @Override
    public String generate(String ownerName, String filePath) {
        return String.format("https://cdn.jsdelivr.net/gh/%s/%s@main/%s", ownerName, repoName, filePath);
    }
}
