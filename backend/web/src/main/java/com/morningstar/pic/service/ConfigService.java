package com.morningstar.pic.service;

import com.morningstar.constant.R;

public interface ConfigService {
    R<Object> setGithubPat(String pat);

    R<String> getGithubPat();
}