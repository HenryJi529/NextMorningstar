export const ROLE_PREFIX = 'ROLE_';

export enum Role {
    SUPER_ADMIN = `${ROLE_PREFIX}super_admin`,
    BLOG_ADMIN = `${ROLE_PREFIX}blog_admin`,
    PROXY_ADMIN = `${ROLE_PREFIX}proxy_admin`,
}

export enum Permission {
    SYS_USER_MANAGE = 'sys:user:manage',
    SYS_PARAM_MANAGE = 'sys:param:manage',
    BLOG_CATEGORY_MANAGE = 'blog:category:manage',
    BLOG_TAG_MANAGE = 'blog:tag:manage',
    BLOG_ARTICLE_MANAGE = 'blog:article:manage',
}
