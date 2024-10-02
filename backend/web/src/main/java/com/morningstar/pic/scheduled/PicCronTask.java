package com.morningstar.pic.scheduled;

import com.morningstar.exception.BaseException;
import com.morningstar.pic.dao.mapper.ConfigMapper;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PicCronTask {
    private final ConfigService configService;

    private final ConfigMapper configMapper;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void clearUselessPats() {
        List<Config> configs = configMapper.selectList(null);
        for (Config config : configs) {
            try {
                configService.validateGithubPat(config.getGithubPat());
                configService.createOrUpdateRepoReadme(config.getUserId());
            } catch (BaseException e) {
                configMapper.deleteById(config.getUserId());
            } catch (RuntimeException ignored) {
            }
        }
    }
}
