package com.morningstar.pic.lib;

public interface ImageLinkGenerator {
    String repoName = "MorningstarPicRepo";

    String generate(String ownerName, String filePath);
}
