drop database if exists morningstar;
create database if not exists morningstar;

use morningstar;

set FOREIGN_KEY_CHECKS = 0;

-- ======================
-- Auth相关
-- ======================

create table if not exists user
(
    id          binary(16),                  # 可通过`UUID_TO_BIN(UUID())`生成
    username    varchar(32) unique not null,
    password    char(60)           not null, # Bcrypt需要60字节存储
    is_locked   boolean            not null default false,
    is_deleted  boolean            not null default false,
    email       varchar(64) unique,
    nickname    varchar(32)        not null,
    avatar      varchar(128),
    create_time datetime,
    update_time datetime,
#     create_user binary(16),
#     update_user binary(16),
    primary key (id)
) comment '用户表';

create table if not exists permission
(
    id     int auto_increment comment '主键',
    name   varchar(32)  not null comment '权限名',
    tag    varchar(128) not null comment 'SpringSecurity授权标识(如sys:user:add)',
    status boolean      not null default true comment '启用情况',
    path   varchar(256)          default null comment '路由地址',
    remark varchar(500)          default null comment '备注',
    primary key (id)
) comment '权限表';

create table if not exists role
(
    id     int auto_increment,
    name   varchar(128) comment '角色名',
    tag    varchar(100) comment '角色标签',
    status boolean default true comment '启用情况',
    remark varchar(500) comment '备注',
    primary key (id)
) comment '角色表';

create table if not exists role_permission
(
    role_id       int comment '角色id',
    permission_id int comment '权限id',
    primary key (role_id, permission_id)
) comment '角色权限关系表';

create table if not exists user_role
(
    user_id binary(16) comment '用户id',
    role_id int comment '角色id',
    primary key (user_id, role_id)
) comment '用户角色关系表';


-- ======================
-- Kill相关
-- ======================

create table if not exists kill_record
(
    id          binary(16),
    content     json,
    create_time datetime,
    update_time datetime,
    primary key (id)
) comment '记录表';

create table if not exists kill_user_record
(
    user_id   binary(16),
    record_id binary(16),
    has_won   boolean,
    primary key (user_id, record_id)
) comment '用户记录关系表';


-- ======================
-- Blog相关
-- ======================

create table if not exists blog_category
(
    id   int auto_increment,
    name varchar(128) unique not null,
    primary key (id)
) comment '博客分类表';

create table if not exists blog_tag
(
    id   int auto_increment,
    name varchar(128) unique not null,
    primary key (id)
) comment '博客标签表';

create table if not exists blog_article
(
    id          bigint,
    title       varchar(128) unique not null comment '标题',
    content     mediumtext          not null comment '正文',
    category_id int                 not null,
    views       int                 not null default 0 comment '访问次数',
    create_time datetime,
    update_time datetime,
    primary key (id)
) comment '博客文章表';

create table if not exists blog_article_tag
(
    article_id bigint,
    tag_id     int,
    primary key (article_id, tag_id)
) comment '博客文章标签关系表';

create table if not exists blog_comment
(
    id          bigint,
    parent_id   bigint,
    user_id     binary(16) not null,
    article_id  bigint     not null,
    content     text       not null,
    create_time datetime,
    update_time datetime,
    primary key (id)
) comment '博客评论表';

create table if not exists blog_comment_user
(
    comment_id  bigint,
    user_id     binary(16) comment '用户id',
    is_approved boolean not null,
    primary key (comment_id, user_id)
) comment '博客评论用户关系表';


-- ======================
-- Proxy相关
-- ======================

create table if not exists proxy_node
(
    id          int auto_increment,
    link        varchar(1024) not null,
    sub_id      int,
    update_time datetime,
    primary key (id)
) comment '代理节点表';

create table if not exists proxy_sub
(
    id          int auto_increment,
    name        varchar(128),
    link        varchar(256) not null,
    update_time datetime,
    primary key (id)
) comment '代理订阅表';

create table if not exists proxy_token
(
    id          int auto_increment,
    name        varchar(128),
    value       varchar(128),
    update_time datetime,
    primary key (id)
) comment '代理令牌表';


-- ======================
-- Pic相关
-- ======================

create table if not exists pic_config
(
    user_id             binary(16),
    secret_key          varchar(32) unique,
    github_pat          varchar(128),
    compression_quality float,
    primary key (user_id)
) comment '图床配置表';

set FOREIGN_KEY_CHECKS = 1;
