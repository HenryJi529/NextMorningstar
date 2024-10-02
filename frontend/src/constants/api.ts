const VITE_API_BASE_PATH = import.meta.env.VITE_API_BASE_PATH;

// USER
export const API_USER_AUTH_LOGIN = VITE_API_BASE_PATH + '/user/auth/login';
export const API_USER_AUTH_REGISTER = VITE_API_BASE_PATH + '/user/auth/register';
export const API_USER_AUTH_LOGOUT = VITE_API_BASE_PATH + '/user/auth/logout';
export const API_USER_AUTH_REFRESH = VITE_API_BASE_PATH + '/user/auth/refresh';
export const API_USER_AUTH_RECOVER_INITIATE = VITE_API_BASE_PATH + '/user/auth/recover/initiate';
export const API_USER_AUTH_RECOVER_COMPLETE = VITE_API_BASE_PATH + '/user/auth/recover/complete';
export const API_USER_AUTH_HEARTBEAT = VITE_API_BASE_PATH + '/user/auth/heartbeat';
export const API_USER_AUTH_OAUTH2_GITHUB = VITE_API_BASE_PATH + '/user/auth/oauth2/github';

export const API_USER_INFO_AVATAR = VITE_API_BASE_PATH + '/user/info/avatar';
export const API_USER_INFO_NICKNAME = VITE_API_BASE_PATH + '/user/info/nickname';
export const API_USER_INFO_PASSWORD = VITE_API_BASE_PATH + '/user/info/password';
export const API_USER_INFO_EMAIL = VITE_API_BASE_PATH + '/user/info/email';
export const API_USER_INFO_PRIVATE = VITE_API_BASE_PATH + '/user/info/private';

export const API_USER_RANDOM_NICKNAME = VITE_API_BASE_PATH + '/user/random/nickname';

export const API_USER_MANAGE_INFO_ALL = VITE_API_BASE_PATH + '/user/manage/info';
export const API_USER_MANAGE_INFO_ONE = VITE_API_BASE_PATH + '/user/manage/info/{id}';
export const API_USER_MANAGE_LOCK = VITE_API_BASE_PATH + '/user/manage/lock';
export const API_USER_MANAGE_RECOVER = VITE_API_BASE_PATH + '/user/manage/recover';
export const API_USER_MANAGE_ROLE = VITE_API_BASE_PATH + '/user/manage/role';

// CONTACT
export const API_CONTACT_EMAIL = VITE_API_BASE_PATH + '/contact/email';

// KILL
export const API_KILL_STATS_SEASON = VITE_API_BASE_PATH + '/kill/stats/season';
export const API_KILL_STATS_DAILY = VITE_API_BASE_PATH + '/kill/stats/daily';
export const API_KILL_RANK_SEASON = VITE_API_BASE_PATH + '/kill/rank/season';

// LOVE
export const API_LOVE_RANDOM = VITE_API_BASE_PATH + '/love/random';
export const API_LOVE_SELECTED = VITE_API_BASE_PATH + '/love/selected';

// BLOG
export const API_BLOG_ARTICLE_TAG = VITE_API_BASE_PATH + '/blog/article/tag/{id}';
export const API_BLOG_ARTICLE_CATEGORY = VITE_API_BASE_PATH + '/blog/article/category/{id}';
export const API_BLOG_ARTICLE_ALL = VITE_API_BASE_PATH + '/blog/article';
export const API_BLOG_ARTICLE_RANDOM = VITE_API_BASE_PATH + '/blog/article/random';
export const API_BLOG_ARTICLE_DETAIL = VITE_API_BASE_PATH + '/blog/article/detail/{id}';
export const API_BLOG_ARTICLE_SEARCH = VITE_API_BASE_PATH + '/blog/article/search';
export const API_BLOG_ARTICLE_COMMENT = VITE_API_BASE_PATH + '/blog/comment/article/{id}';
export const API_BLOG_CATEGORY_ALL = VITE_API_BASE_PATH + '/blog/category';
export const API_BLOG_CATEGORY_ONE = VITE_API_BASE_PATH + '/blog/category/{id}';
export const API_BLOG_TAG_ALL = VITE_API_BASE_PATH + '/blog/tag';
export const API_BLOG_TAG_ONE = VITE_API_BASE_PATH + '/blog/tag/{id}';
export const API_BLOG_COMMENT_ALL = VITE_API_BASE_PATH + '/blog/comment';
export const API_BLOG_COMMENT_THUMBS_UP = VITE_API_BASE_PATH + '/blog/comment/thumbs-up/{id}';
export const API_BLOG_COMMENT_THUMBS_DOWN = VITE_API_BASE_PATH + '/blog/comment/thumbs-down/{id}';
export const API_BLOG_COMMENT_THUMBS_NONE = VITE_API_BASE_PATH + '/blog/comment/thumbs-none/{id}';
export const API_BLOG_FEED_RSS = VITE_API_BASE_PATH + '/blog/feed/rss.xml';
export const API_BLOG_FEED_ATOM = VITE_API_BASE_PATH + '/blog/feed/atom.xml';

// PROXY
export const API_PROXY_NODE_ALL = VITE_API_BASE_PATH + '/proxy/node';
export const API_PROXY_NODE_ONE = VITE_API_BASE_PATH + '/proxy/node/{id}';
export const API_PROXY_SUB_ALL = VITE_API_BASE_PATH + '/proxy/sub';
export const API_PROXY_SUB_ONE = VITE_API_BASE_PATH + '/proxy/sub/{id}';
export const API_PROXY_TOKEN_ALL = VITE_API_BASE_PATH + '/proxy/token';
export const API_PROXY_TOKEN_ONE = VITE_API_BASE_PATH + '/proxy/token/{id}';

// COMMON
export const API_COMMON_CAPTCHA_IMAGE = VITE_API_BASE_PATH + '/common/captcha/image';
export const API_COMMON_CAPTCHA_EMAIL = VITE_API_BASE_PATH + '/common/captcha/email';
export const API_COMMON_QINIU_UPLOAD = VITE_API_BASE_PATH + '/common/qiniu/upload';
export const API_COMMON_QRCODE = VITE_API_BASE_PATH + '/common/qrcode';

// WEBSOCKET
export const API_WEBSOCKET_CONNECTION = VITE_API_BASE_PATH + '/ws-connection';
