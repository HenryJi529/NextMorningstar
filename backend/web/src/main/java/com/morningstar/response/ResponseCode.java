package com.morningstar.response;

import com.morningstar.constant.GithubConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 业务响应码
 */
@Getter
@Schema(description = "业务响应码")
public enum ResponseCode {
    SUCCESS("操作成功"),
    ERROR("操作失败"),

    CHECK_CODE_TIMEOUT("验证码过期"),
    CHECK_CODE_ERROR("验证码错误"),
    ID_MISMATCH("请求体中的id与路径id不一致"),

    AUTHENTICATION_FAILED("认证失败"),
    TOKEN_INVALID("凭证(令牌)无效"),
    TOKEN_EXPIRED("凭证(令牌)过期"),
    USERNAME_OR_PASSWORD_ERROR("用户名或密码错误"),
    NO_PERMISSION("没有权限访问该资源"),
    ACCOUNT_INFO_ERROR("账号信息错误(可能出现了重复)"),
    USERNAME_INVALID(String.format("用户名不能以`%s`开头，该前缀为系统保留字段", GithubConstant.GITHUB_USER_PREFIX)),
    USERNAME_ALREADY_EXISTS("用户名已存在"),
    ACCOUNT_NOT_FOUND("账号不存在"),
    EMAIL_ALREADY_EXISTS("邮箱已存在"),
    ACCOUNT_LOCKED("账号已锁定"),
    ROLE_NOT_EXISTS("角色不存在"),

    SYS_PARAM_CREATE_FAILED("系统参数创建失败"),
    SYS_PARAM_UPDATE_FAILED("系统参数[%d]更新失败"),
    SYS_PARAM_DELETE_FAILED("系统参数[%d]删除失败"),
    SYS_PARAM_ID_NOT_FOUND("系统参数[%d]不存在"),
    SYS_PARAM_NAME_NOT_FOUND("系统参数\"%s\"不存在"),

    PIC_GITHUB_PAT_NOT_FOUND("Github PAT尚未设置"),
    PIC_GITHUB_PAT_INVALID("Github PAT无效"),
    PIC_GITHUB_REPO_ACCESS_DENIED("当前Github PAT(%s)无法修改指定的图床仓库"),
    PIC_TOTAL_DAY_QUOTA_EXCEEDED("超过使用天数限额，无法继续上传图片"),
    PIC_IMAGE_FORMAT_NOT_SUPPORTED("图片[%s]的格式不支持"),
    PIC_IMAGE_INVALID_CONTENT_TYPE("图片[%s]的Content-Type[%s]不合法"),
    PIC_IMAGE_INVALID_FILENAME("图片[%s]的文件名不合法"),
    PIC_IMAGE_NOT_FOUND("图片[%s]不存在"),
    PIC_IMAGE_UPLOAD_FAILED("图片[%s]上传失败"),
    PIC_INVALID_COMPRESSION_QUALITY("压缩质量[%.2f]不在(0, 1]的范围内"),
    PIC_SECRET_KEY_NOT_FOUND("图床secretKey[%s]不存在"),

    PROXY_NODE_CONTENT_INVALID("代理节点[%s]内容无效"),
    PROXY_NODE_TYPE_UNSUPPORTED("代理节点[%s]类型不支持"),

    SYS_PARAM_ACCESS_DENIED("您无权访问系统参数[%s]"),

    BLOG_ARTICLE_NOT_FOUND("博客文章[%d]不存在"),
    BLOG_CATEGORY_NOT_FOUND("博客分类[%d]不存在"),
    BLOG_TAG_NOT_FOUND("博客标签[%d]不存在"),
    BLOG_TAG_NAME_ALREADY_EXISTS("博客标签\"%s\"已存在"),
    BLOG_TAG_UPDATE_FAILED("博客标签[%d]更新失败"),
    BLOG_CATEGORY_NAME_ALREADY_EXISTS("博客分类\"%s\"已存在"),
    BLOG_CATEGORY_DELETE_FAILED("博客分类[%d]删除失败"),
    BLOG_COMMENT_NOT_FOUND("博客评论[%d]不存在");


    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }
}