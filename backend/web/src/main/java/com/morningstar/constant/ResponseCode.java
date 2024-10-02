package com.morningstar.constant;

import cn.hutool.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 业务响应码
 */
@Getter
@Schema(description = "业务响应码")
public enum ResponseCode {
    SUCCESS(1, "操作成功"),

    ERROR(0, "操作失败"),

    CHECK_CODE_TIMEOUT(-40001, "验证码过期"),
    CHECK_CODE_ERROR(-40002, "验证码错误"),
    ID_MISMATCH(-40003, "请求体中的id与路径id不一致"),

    AUTHENTICATION_FAILED(-HttpStatus.HTTP_UNAUTHORIZED, "认证失败"),
    TOKEN_INVALID(-40101, "凭证(令牌)无效"),
    TOKEN_EXPIRED(-40102, "凭证(令牌)过期"),

    NO_PERMISSION(-HttpStatus.HTTP_FORBIDDEN, "没有权限访问该资源"),

    USERNAME_OR_PASSWORD_ERROR(0, "用户名或密码错误"),
    ACCOUNT_INFO_ERROR(0, "账号信息错误(可能出现了重复)"),
    USERNAME_INVALID(0, String.format("用户名不能以`%s`开头，该前缀为系统保留字段", GithubConstant.GITHUB_USER_PREFIX)),
    USERNAME_ALREADY_EXISTS(0, "用户名已存在"),
    ACCOUNT_NOT_FOUND(0, "账号不存在"),
    EMAIL_ALREADY_EXISTS(0, "邮箱已存在"),
    ACCOUNT_LOCKED(0, "账号已锁定"),

    ROLE_NOT_EXISTS(0, "角色不存在");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}