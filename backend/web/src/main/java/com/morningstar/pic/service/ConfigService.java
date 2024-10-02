package com.morningstar.pic.service;


public interface ConfigService {
    void setGithubPat(String pat);

    String getGithubPat();

    String getGithubAccount();

    void deleteGithubPat();
}