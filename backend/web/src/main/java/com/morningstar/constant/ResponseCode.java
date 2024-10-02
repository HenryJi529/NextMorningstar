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

    TOKEN_INVALID(-1, "凭证(令牌)无效"),
    TOKEN_EXPIRED(-2, "凭证(令牌)过期"),

    NOT_IMPLEMENT(-3, "方法未实现"),

    CHECK_CODE_TIMEOUT(-4, "验证码过期"),
    CHECK_CODE_ERROR(-5, "验证码错误"),

    AUTHENTICATION_FAILED(-HttpStatus.HTTP_UNAUTHORIZED, "认证失败"),
    NO_PERMISSION(-HttpStatus.HTTP_FORBIDDEN, "没有权限访问该资源"),

    ERROR(0, "操作失败"),

    ID_MISMATCH(0, "请求体中的id与路径id不一致"),


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