package com.morningstar.pic.service;


import com.morningstar.pic.pojo.po.Config;

import java.util.UUID;

public interface ConfigService {
    void setGithubPat(String pat, UUID userId);

    void validateGithubPat(String pat);

    Config getConfig(UUID userId);

    Config getConfig(String secretKey);

    String getGithubPat(UUID userId);

    String getGithubAccount(UUID userId);

    void deleteGithubPat(UUID userId);

    Float getCompressionQuality(UUID userId);

    void setCompressionQuality(Float compressionQuality, UUID userId);

    String getSecretKey(UUID userId);

    void createOrUpdateRepoReadme(UUID userId);
}