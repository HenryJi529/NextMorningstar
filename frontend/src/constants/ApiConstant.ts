// USER
export const API_USER_AUTH_LOGIN = "/api/user/auth/login";
export const API_USER_AUTH_REGISTER = "/api/user/auth/register";
export const API_USER_AUTH_LOGOUT = "/api/user/auth/logout";
export const API_USER_AUTH_REFRESH = "/api/user/auth/refresh";
export const API_USER_AUTH_RECOVER_INITIATE = "/api/user/auth/recover/initiate";
export const API_USER_AUTH_RECOVER_COMPLETE = "/api/user/auth/recover/complete";
export const API_USER_AUTH_HEARTBEAT = "/api/user/auth/heartbeat";
export const API_USER_AUTH_OAUTH2_GITHUB = "/api/user/auth/oauth2/github";

export const API_USER_INFO_AVATAR = "/api/user/info/avatar";
export const API_USER_INFO_NICKNAME = "/api/user/info/nickname";
export const API_USER_INFO_PASSWORD = "/api/user/info/password";
export const API_USER_INFO_EMAIL = "/api/user/info/email";
export const API_USER_INFO_PRIVATE = "/api/user/info/private";

export const API_USER_RANDOM_NICKNAME = "/api/user/random/nickname";

export const API_USER_MANAGE_INFO_ALL = "/api/user/manage/info";
export const API_USER_MANAGE_INFO_ONE = "/api/user/manage/info/{id}";
export const API_USER_MANAGE_LOCK = "/api/user/manage/lock";
export const API_USER_MANAGE_RECOVER = "/api/user/manage/recover";
export const API_USER_MANAGE_ROLE = "/api/user/manage/role";


// CONTACT
export const API_CONTACT_EMAIL = "/api/contact/email";


// KILL
export const API_KILL_STATS_SEASON = "/api/kill/stats/season";
export const API_KILL_STATS_DAILY = "/api/kill/stats/daily";
export const API_KILL_RANK_SEASON = "/api/kill/rank/season";


// LOVE
export const API_LOVE_RANDOM = "/api/love/random";
export const API_LOVE_SELECTED = "/api/love/selected";


// BLOG
export const API_BLOG_ARTICLE_TAG = "/api/blog/article/tag/{id}";
export const API_BLOG_ARTICLE_CATEGORY = "/api/blog/article/category/{id}";
export const API_BLOG_ARTICLE_ALL = "/api/blog/article";
export const API_BLOG_ARTICLE_RANDOM = "/api/blog/article/random";
export const API_BLOG_ARTICLE_DETAIL = "/api/blog/article/detail/{id}";
export const API_BLOG_ARTICLE_SEARCH = "/api/blog/article/search";
export const API_BLOG_ARTICLE_COMMENT = "/api/blog/comment/article/{id}";
export const API_BLOG_CATEGORY_ALL = "/api/blog/category";
export const API_BLOG_CATEGORY_ONE = "/api/blog/category/{id}";
export const API_BLOG_TAG_ALL = "/api/blog/tag";
export const API_BLOG_TAG_ONE = "/api/blog/tag/{id}";
export const API_BLOG_COMMENT_ALL = "/api/blog/comment";
export const API_BLOG_COMMENT_THUMBS_UP = "/api/blog/comment/thumbs-up/{id}";
export const API_BLOG_COMMENT_THUMBS_DOWN = "/api/blog/comment/thumbs-down/{id}";
export const API_BLOG_COMMENT_THUMBS_NONE = "/api/blog/comment/thumbs-none/{id}";
export const API_BLOG_FEED_RSS = "/api/blog/feed/rss.xml";
export const API_BLOG_FEED_ATOM = "/api/blog/feed/atom.xml";


// PROXY
export const API_PROXY_NODE_ALL = "/api/proxy/node";
export const API_PROXY_NODE_ONE = "/api/proxy/node/{id}";
export const API_PROXY_SUB_ALL = "/api/proxy/sub";
export const API_PROXY_SUB_ONE = "/api/proxy/sub/{id}";
export const API_PROXY_TOKEN_ALL = "/api/proxy/token";
export const API_PROXY_TOKEN_ONE = "/api/proxy/token/{id}";


// COMMON
export const API_COMMON_CAPTCHA_IMAGE = "/api/common/captcha/image";
export const API_COMMON_CAPTCHA_EMAIL = "/api/common/captcha/email";
export const API_COMMON_QINIU_UPLOAD = "/api/common/qiniu/upload";
export const API_COMMON_QRCODE = "/api/common/qrcode";


// WEBSOCKET
export const API_WEBSOCKET_CONNECTION = "/api/ws-connection";