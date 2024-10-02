package com.morningstar.pic.lib;

public class GithubImageLinkGenerator implements ImageLinkGenerator {
    @Override
    public String generate(String ownerName, String filePath) {
        return String.format("https://github.com/%s/%s/raw/main/%s", ownerName, repoName, filePath);
    }
}