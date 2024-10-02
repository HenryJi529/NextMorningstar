package com.morningstar.kill.pojo.vo.resp;

import cn.hutool.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 业务响应码
 */
@Getter
@Schema(description = "业务响应码")
public enum ResponseCode{
    SUCCESS(1,"操作成功"),
    ERROR(0,"操作失败"),
    NOT_IMPLEMENT(6, "方法未实现"),
    DATA_ERROR(0,"参数异常"),
    NO_RESPONSE_DATA(0,"无响应数据"),
    CHECK_CODE_NOT_EMPTY(0,"验证码不能为空"),
    CHECK_CODE_TIMEOUT(0, "验证码过期"),
    CHECK_CODE_ERROR(0,"验证码错误"),
    USERNAME_OR_PASSWORD_ERROR(0,"用户名或密码错误"),
    ACCOUNT_INFO_ERROR(0, "账号信息错误(可能出现了重复)"),
    ACCOUNT_EXISTS_ERROR(0,"该账号已存在"),
    ACCOUNT_NOT_EXISTS(0,"该账号不存在"),
    TOKEN_ERROR(2,"凭证(令牌)错误"),
    AUTHENTICATION_FAILED(3,"认证失败"),
    BAD_REQUEST(HttpStatus.HTTP_BAD_REQUEST, "请求有错误"),
    NO_PERMISSION(HttpStatus.HTTP_FORBIDDEN,"没有权限访问该资源"),
    NOT_FOUND(HttpStatus.HTTP_NOT_FOUND, "资源不存在"),
    BAD_METHOD(HttpStatus.HTTP_BAD_METHOD, "请求方法错误"),
    INVALID_TOKEN(0,"无效的票据"),
    OPERATION_MENU_PERMISSION_CATALOG_ERROR(0,"操作后的菜单类型是目录，所属菜单必须为默认顶级菜单或者目录"),
    OPERATION_MENU_PERMISSION_MENU_ERROR(0,"操作后的菜单类型是菜单，所属菜单必须为目录类型"),
    OPERATION_MENU_PERMISSION_BTN_ERROR(0,"操作后的菜单类型是按钮，所属菜单必须为菜单类型"),
    OPERATION_MENU_PERMISSION_URL_CODE_NULL(0,"菜单权限的按钮标识不能为空"),
    ROLE_PERMISSION_RELATION(0, "该菜单权限存在子集关联，不允许删除");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}