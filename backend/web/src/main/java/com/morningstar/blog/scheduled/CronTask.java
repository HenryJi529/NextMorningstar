package com.morningstar.blog.scheduled;

import com.morningstar.blog.service.ArticleService;
import com.morningstar.constant.RedisConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component("blogCronTask")
@RequiredArgsConstructor
@Slf4j
public class CronTask {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ArticleService articleService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncArticleViewsToDatabase(){
        Set<String> keys = redisTemplate.keys(RedisConstant.BLOG_VIEWS + RedisConstant.KEY_SEPARATOR + "*");
        for (String key : keys) {
            // 解析ID
            Long articleId = Long.valueOf(key.substring(key.lastIndexOf(RedisConstant.KEY_SEPARATOR) + 1));
            // 解析访问增量
            int viewsDelta = (Integer) Objects.requireNonNull(redisTemplate.opsForValue().get(key));
            // 单文章同步
            articleService.syncArticleViewByArticleId(articleId, viewsDelta);
            // 清空访问
            redisTemplate.delete(key);
        }
        log.info("文章访问量同步完成...");
    }
}
