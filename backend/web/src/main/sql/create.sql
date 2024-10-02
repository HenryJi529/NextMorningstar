drop database if exists morningstar;
create database if not exists morningstar;

use morningstar;

set FOREIGN_KEY_CHECKS = 0;

-- 构建用户表
create table if not exists user (
    id binary(16), # 可通过`UUID_TO_BIN(UUID())`生成
    username varchar(32) unique not null,
    password char(60) not null, # Bcrypt需要60字节存储
    is_locked boolean not null default false,
    is_admin boolean not null default false,
    is_online boolean not null default false,
    is_deleted boolean not null default false,
    phone char(11) unique,
    email varchar(64) unique,
    nickname varchar(32) not null,
    sex enum('男', '女') not null,
    avatar varchar(128),
    create_time datetime,
    update_time datetime,
    primary key (id)
) comment '用户表';


-- 构建记录表
create table if not exists record (
    id binary(16),
    content json,
    create_time datetime,
    update_time datetime,
    primary key (id)
) comment '记录表';


-- 构建用户记录关系表
create table if not exists user_record (
    user_id binary(16),
    record_id binary(16),
    has_won boolean,
    primary key (user_id, record_id)
) comment '用户记录关系表';


-- 构建权限表
create table if not exists permission (
    id int auto_increment comment '主键',
    name varchar(32) not null comment '权限名',
    tag varchar(128) not null comment 'SpringSecurity授权标识(如sys:user:add)',
    status boolean not null default true comment '启用情况',
    path varchar(256) default null comment '路由地址',
    remark varchar(500) default null comment '备注',
    primary key (id)
) comment '权限表';


-- 构建角色表
create table if not exists role (
  id int auto_increment,
  name varchar(128) comment '角色名',
  tag varchar(100) comment '角色标签',
  status boolean default true comment '启用情况',
  remark varchar(500) comment '备注',
  primary key (id)
) comment '角色表';


-- 构建角色权限关系表
create table if not exists role_permission (
  role_id int not null comment '角色id',
  permission_id int not null comment '权限id',
  primary key (role_id, permission_id)
) comment '角色权限关系表';


-- 构建用户角色关系表
create table if not exists user_role (
  user_id binary(16) not null comment '用户id',
  role_id int not null comment '角色id',
  primary key (user_id, role_id)
) comment '用户角色关系表';


set FOREIGN_KEY_CHECKS = 1;
