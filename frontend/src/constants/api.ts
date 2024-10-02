/* USER */
export const API_USER_AUTH_LOGIN = '/user/auth/login';
export const API_USER_AUTH_REGISTER = '/user/auth/register';
export const API_USER_AUTH_LOGOUT = '/user/auth/logout';
export const API_USER_AUTH_REFRESH = '/user/auth/refresh';
export const API_USER_AUTH_RECOVER_INITIATE = '/user/auth/recover/initiate';
export const API_USER_AUTH_RECOVER_COMPLETE = '/user/auth/recover/complete';
export const API_USER_AUTH_HEARTBEAT = '/user/auth/heartbeat';
export const API_USER_AUTH_OAUTH2_GITHUB = '/user/auth/oauth2/github';

export const API_USER_INFO_AVATAR = '/user/info/avatar';
export const API_USER_INFO_NICKNAME = '/user/info/nickname';
export const API_USER_INFO_PASSWORD = '/user/info/password';
export const API_USER_INFO_EMAIL = '/user/info/email';
export const API_USER_INFO_PRIVATE = '/user/info/private';

export const API_USER_RANDOM_NICKNAME = '/user/random/nickname';

export const API_USER_MANAGE_INFO_ALL = '/user/manage/info';
export const API_USER_MANAGE_INFO_ONE = '/user/manage/info/{id}';
export const API_USER_MANAGE_LOCK = '/user/manage/lock';
export const API_USER_MANAGE_RECOVER = '/user/manage/recover';
export const API_USER_MANAGE_ROLE = '/user/manage/role';

export const API_SYSTEM_PARAM_ALL = '/system/param';
export const API_SYSTEM_PARAM_ONE = '/system/param/{id}';

/* CONTACT */
export const API_CONTACT_EMAIL = '/contact/email';

/* LOVE */
export const API_LOVE_RANDOM = '/love/random';
export const API_LOVE_SELECTED = '/love/selected';

/* BLOG */
export const API_BLOG_ARTICLE_TAG = '/blog/article/tag/{id}';
export const API_BLOG_ARTICLE_CATEGORY = '/blog/article/category/{id}';
export const API_BLOG_ARTICLE_ALL = '/blog/article';
export const API_BLOG_ARTICLE_RANDOM = '/blog/article/random';
export const API_BLOG_ARTICLE_DETAIL = '/blog/article/detail/{id}';
export const API_BLOG_ARTICLE_SEARCH = '/blog/article/search';
export const API_BLOG_ARTICLE_COMMENT = '/blog/comment/article/{id}';
export const API_BLOG_CATEGORY_ALL = '/blog/category';
export const API_BLOG_CATEGORY_ONE = '/blog/category/{id}';
export const API_BLOG_TAG_ALL = '/blog/tag';
export const API_BLOG_TAG_ONE = '/blog/tag/{id}';
export const API_BLOG_COMMENT_ALL = '/blog/comment';
export const API_BLOG_COMMENT_THUMBS_UP = '/blog/comment/thumbs-up/{id}';
export const API_BLOG_COMMENT_THUMBS_DOWN = '/blog/comment/thumbs-down/{id}';
export const API_BLOG_COMMENT_THUMBS_NONE = '/blog/comment/thumbs-none/{id}';
export const API_BLOG_FEED_RSS = '/blog/feed/rss.xml';
export const API_BLOG_FEED_ATOM = '/blog/feed/atom.xml';

/* PROXY */
export const API_PROXY_NODE_ALL = '/proxy/node';
export const API_PROXY_NODE_ONE = '/proxy/node/{id}';
export const API_PROXY_SUB_ALL = '/proxy/sub';
export const API_PROXY_SUB_ONE = '/proxy/sub/{id}';
export const API_PROXY_TOKEN_ALL = '/proxy/token';
export const API_PROXY_TOKEN_ONE = '/proxy/token/{id}';

/* COMMON */
export const API_COMMON_CAPTCHA_IMAGE = '/common/captcha/image';
export const API_COMMON_CAPTCHA_EMAIL = '/common/captcha/email';
export const API_COMMON_QINIU_UPLOAD = '/common/qiniu/upload';
export const API_COMMON_QRCODE = '/common/qrcode';

/* WEBSOCKET */
export const API_WEBSOCKET_CHAT = '/ws/chat';
