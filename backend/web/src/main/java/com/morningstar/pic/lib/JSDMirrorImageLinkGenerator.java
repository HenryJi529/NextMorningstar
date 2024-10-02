package com.morningstar.pic.lib;

public class JSDMirrorImageLinkGenerator implements ImageLinkGenerator {
    @Override
    public String generate(String ownerName, String filePath) {
        return String.format("https://cdn.jsdmirror.com/gh/%s/%s@main/%s", ownerName, repoName, filePath);
    }
}
