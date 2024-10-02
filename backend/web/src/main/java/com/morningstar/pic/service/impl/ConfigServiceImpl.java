package com.morningstar.pic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.morningstar.constant.RedisConstant;
import com.morningstar.exception.BaseException;
import com.morningstar.exception.PicGithubPatUnsetException;
import com.morningstar.pic.dao.mapper.ConfigMapper;
import com.morningstar.pic.lib.ImageLinkGenerator;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.util.GithubUtil;
import com.morningstar.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    private final GithubUtil githubUtil;

    public void validateGithubPat(String pat) {
        try {
            githubUtil.getUser(pat);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BaseException("Github Pat无效");
        }
        // 判断仓库是否存在
        try {
            // 如果存在，判断能否修改目标仓库
            githubUtil.getRepo(pat, ImageLinkGenerator.repoName);

            try {
                String filename = "test/" + new Date().getTime() + ".gif";
                // NOTE: 透明空白1x1图片
                String content = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";
                githubUtil.createFile(pat, ImageLinkGenerator.repoName, filename, content);
                githubUtil.deleteFile(pat, ImageLinkGenerator.repoName, filename);
            } catch (HttpClientErrorException.Forbidden e) {
                throw new BaseException(String.format("Github Pat无法修改指定的图床仓库(%s)", ImageLinkGenerator.repoName));
            }
        } catch (HttpClientErrorException.NotFound e) {
            // 如果不存在，判断能否创建仓库
            try {
                githubUtil.createPublicRepo(pat, ImageLinkGenerator.repoName);
            } catch (HttpClientErrorException.Forbidden e1) {
                throw new BaseException(String.format("Github Pat无法创建指定的图床仓库(%s)", ImageLinkGenerator.repoName));
            }
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#userId"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#userId")
    })
    public void setGithubPat(String pat, UUID userId) {
        // 判断PAT是否有效
        validateGithubPat(pat);

        // 将配置存入数据库
        if (configMapper.selectById(userId) == null) {
            configMapper.insert(
                    Config
                            .builder()
                            .userId(userId)
                            .githubPat(pat)
                            .secretKey(RandomUtil.getString(32))
                            .build()
            );
        } else {
            configMapper.updateById(
                    Config
                            .builder()
                            .userId(userId)
                            .githubPat(pat)
                            .build()
            );
        }

        // 创建或更新仓库README
        createOrUpdateRepoReadme(userId);
    }

    @Override
    public Config getConfig(UUID userId) {
        return configMapper.selectById(userId);
    }

    @Override
    public Config getConfig(String secretKey) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getSecretKey, secretKey);
        return configMapper.selectOne(wrapper);
    }

    @Override
    public Float getCompressionQuality(UUID userId) {
        Config config = getConfig(userId);
        if (config == null) {
            return null;
        } else {
            return config.getCompressionQuality() == null ? null : config.getCompressionQuality();
        }
    }

    @Override
    public void setCompressionQuality(Float compressionQuality, UUID userId) {
        if (configMapper.selectById(userId) == null) {
            throw new PicGithubPatUnsetException();
        }
        LambdaUpdateWrapper<Config> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Config::getUserId, userId);
        wrapper.set(Config::getCompressionQuality, compressionQuality);
        configMapper.update(wrapper);
    }

    @Override
    public String getSecretKey(UUID userId) {
        Config config = getConfig(userId);
        return config == null ? null : config.getSecretKey();
    }

    @Override
    @Async
    public void createOrUpdateRepoReadme(UUID userId) {
        Config config = configMapper.selectById(userId);
        try {
            githubUtil.getFileOrDir(config.getGithubPat(), ImageLinkGenerator.repoName, "README.md");
            githubUtil.deleteFile(config.getGithubPat(), ImageLinkGenerator.repoName, "README.md");
        } catch (HttpClientErrorException.NotFound ignored) {
        }

        try {
            byte[] bytesContent = Objects.requireNonNull(getClass().getResourceAsStream("/media/pic/README.md")).readAllBytes();
            githubUtil.createFile(config.getGithubPat(), ImageLinkGenerator.repoName, "README.md", bytesContent);
        } catch (IOException ignored) {
        }
    }

    @Override
    public String getGithubPat(UUID userId) {
        Config config = getConfig(userId);
        return config == null ? null : config.getGithubPat();
    }

    @Override
    public String getGithubAccount(UUID userId) {
        Config config = configMapper.selectById(userId);
        if (config == null) {
            throw new PicGithubPatUnsetException();
        }
        return githubUtil.getUserAsAccount(config.getGithubPat()).getUsername();
    }

    @Override
    public void deleteGithubPat(UUID userId) {
        if (configMapper.deleteById(userId) != 1) {
            throw new PicGithubPatUnsetException();
        }
    }
}