package com.morningstar.pic.service;


import com.morningstar.pic.pojo.po.Config;

import java.util.UUID;

public interface ConfigService {
    void setGithubPat(String pat, UUID userId);

    Config getConfig(UUID userId);

    Config getConfig(String secretKey);

    String getGithubPat(UUID userId);

    String getGithubAccount(UUID userId);

    void deleteGithubPat(UUID userId);
}