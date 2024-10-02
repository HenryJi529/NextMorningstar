package com.morningstar.constant;

public class RedisConstant {
    /**
     * 验证码的前缀
     */
    public static final String CHECK_PREFIX = "CK:";

    /**
     * UserDetails的前缀
     */
    public static final String LOGIN_PREFIX = "LOGIN:";

    /**
     * 恢复账号的前缀
     */
    public static final String RECOVER_PREFIX = "RECOVER:";

    /**
     * 心跳检测的前缀
     */
    public static final String HEARTBEAT_PREFIX = "HEARTBEAT:";


    /**
     * 缓存相关前缀
     */
    public static final String CACHE_BLOG_ARTICLE = "BLOG_ARTICLE";
    public static final String CACHE_PIC_IMAGES = "PIC_IMAGES";
    public static final String CACHE_PIC_DIRS = "PIC_DIRS";
}
