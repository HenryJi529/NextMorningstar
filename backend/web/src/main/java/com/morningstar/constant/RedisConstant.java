package com.morningstar.constant;

public class RedisConstant {
    /**
     * 前缀后的分隔符
     */
    public static final String KEY_SEPARATOR = ":";

    /**
     * 认证相关前缀
     */
    public static final String AUTH_RECOVER = "auth:recover"; // 恢复账号
    public static final String AUTH_LOGIN = "auth:login"; // UserDetails
    public static final String AUTH_CHECK = "auth:check"; // 验证码
    public static final String AUTH_HEARTBEAT = "auth:heartbeat"; // 心跳检测

    /**
     * 博客相关前缀
     */
    public static final String BLOG_VIEWS = "blog:views"; // 博客访问量

    /**
     * 缓存相关前缀
     */
    public static final String CACHE_BLOG_ARTICLE = "cache:blog_article";
    public static final String CACHE_PIC_IMAGES = "cache:pic_images";
    public static final String CACHE_PIC_DIRS = "cache:pic_dirs";
}
