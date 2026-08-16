export const ROLE_PREFIX = 'ROLE_';

export enum Role {
    SYSTEM_ADMIN = `${ROLE_PREFIX}system_admin`,
    BLOG_ADMIN = `${ROLE_PREFIX}blog_admin`,
    PROXY_ADMIN = `${ROLE_PREFIX}proxy_admin`,
    DEV_ADMIN = `${ROLE_PREFIX}dev_admin`,
}

export enum Permission {
    SYS_USER_MANAGE = 'sys:user:manage',
    SYS_PARAM_MANAGE = 'sys:param:manage',
    BLOG_CATEGORY_MANAGE = 'blog:category:manage',
    BLOG_TAG_MANAGE = 'blog:tag:manage',
    BLOG_ARTICLE_MANAGE = 'blog:article:manage',
    DEV_RUN_CANCEL = 'dev:run:cancel',
    DEV_PROJECT_SCHEDULE = 'dev:project:schedule',
}
