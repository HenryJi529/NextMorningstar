package com.morningstar.common.web.controller;

import com.morningstar.common.pojo.bo.UserEditableInfo;
import com.morningstar.common.pojo.bo.UserPrivateInfo;
import com.morningstar.common.pojo.bo.UserPublicInfo;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.common.service.UserService;
import com.morningstar.response.PageResult;
import com.morningstar.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "用户相关接口定义")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "登入")
    @PostMapping("/auth/login")
    public R<LoginResponseVo> login(@Valid @RequestBody LoginRequestVo vo) {
        return R.ok(userService.login(vo));
    }

    @Operation(summary = "注册")
    @PostMapping("/auth/register")
    public R<RegisterResponseVo> register(@Valid @RequestBody RegisterRequestVo vo) {
        return R.ok(userService.register(vo));
    }

    @Operation(summary = "登出")
    @PostMapping("/auth/logout")
    public R<LogoutResponseVo> logout() {
        return R.ok(userService.logout());
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/auth/refresh")
    public R<String> refresh() {
        return R.ok(userService.refreshToken());
    }

    @Operation(summary = "心跳检测")
    @GetMapping("/auth/heartbeat")
    public R<Object> heartbeat() {
        userService.heartbeat();
        return R.ok();
    }

    @Operation(summary = "发起账号恢复")
    @PostMapping("/auth/recover/initiate")
    public R<Object> initiateRecovery(@Valid @RequestBody InitiateRecoveryRequestVo vo) {
        userService.initiateRecovery(vo);
        return R.ok();
    }

    @Operation(summary = "完成账号恢复")
    @PostMapping("/auth/recover/complete")
    public R<CompleteRecoveryResponseVo> completeRecovery(@Valid @RequestBody CompleteRecoveryRequestVo vo) {
        return R.ok(userService.completeRecovery(vo));
    }

    @Operation(summary = "GithubOAuth2回调地址")
    @GetMapping("/auth/oauth2/github")
    public R<OAuthResponseVo> githubOAuthCallback(@RequestParam("code") String code) {
        return R.ok(userService.githubOAuthCallback(code));
    }

    @Operation(summary = "查询私有信息(根据token)")
    @GetMapping("/info/private")
    public R<UserPrivateInfo> getPrivateInfo() {
        return R.ok(userService.getPrivateInfo());
    }

    @Operation(summary = "更新邮箱")
    @PatchMapping("/info/email")
    public R<UserEditableInfo> updateEmail(@Valid @RequestBody UpdateEmailRequestVo vo) {
        return R.ok(userService.updateEmail(vo));
    }

    @Operation(summary = "更新密码")
    @PatchMapping("/info/password")
    public R<UserEditableInfo> updatePassword(@Valid @RequestBody UpdatePasswordRequestVo vo) {
        return R.ok(userService.updatePassword(vo));
    }

    @Operation(summary = "更新昵称")
    @PatchMapping("/info/nickname")
    public R<UserEditableInfo> updateNickname(@Valid @RequestBody UpdateNicknameRequestVo vo) {
        return R.ok(userService.updateNickname(vo));
    }

    @Operation(summary = "更新头像")
    @PatchMapping("/info/avatar")
    public R<UserEditableInfo> updateAvatar(@Valid @RequestBody UpdateAvatarRequestVo vo) {
        return R.ok(userService.updateAvatar(vo));
    }

    @Operation(summary = "随机获取昵称")
    @GetMapping("/random/nickname")
    public R<String> getRandomNickname(@RequestParam(value = "latitude", required = false) Double latitude, @RequestParam(value = "longitude", required = false) Double longitude) {
        if (latitude == null || longitude == null) {
            return R.ok(userService.getRandomNickname());
        } else {
            return R.ok(userService.getRandomNickname(latitude, longitude));
        }
    }

    @Operation(summary = "精确查询公开信息(根据用username)")
    @Parameters({
            @Parameter(name = "username", description = "用户名", in = ParameterIn.PATH, required = true),
            @Parameter(name = HttpHeaders.AUTHORIZATION, description = "JWT", in = ParameterIn.HEADER),
    })
    @GetMapping("/discover/exact/{username}")
    public R<UserPublicInfo> getPublicInfo(@PathVariable String username) {
        return R.ok(userService.getPublicInfo(username));
    }

    @Operation(summary = "模糊查询公开信息")
    @GetMapping("/discover/fuzzy")
    public R<PageResult<UserPublicInfo>> getPublicInfo(@Valid UserPublicInfoFuzzyPageQueryRequestVo vo) {
        return R.ok(userService.getPublicInfo(vo));
    }

    @Operation(summary = "查询私有信息(全部)")
    @GetMapping("/manage/info")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<List<UserPrivateInfo>> getAllPrivateInfo() {
        return R.ok(userService.getAllPrivateInfo());
    }

    @Operation(summary = "查询私有信息(根据id)")
    @GetMapping("/manage/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<UserPrivateInfo> getPrivateInfo(@PathVariable UUID id) {
        return R.ok(userService.getPrivateInfo(id));
    }

    @Operation(summary = "锁定/解锁账号(管理员)")
    @GetMapping("/manage/lock")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<Object> lock(@RequestParam("id") UUID id, @RequestParam("isLocked") Boolean isLocked) {
        userService.updateIsLocked(id, isLocked);
        return R.ok();
    }

    @Operation(summary = "发起账号恢复(管理员)")
    @GetMapping("/manage/recover")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<Object> recover(@RequestParam("id") UUID id, @RequestParam("email") @Email(message = "邮箱必须有效") String email) {
        userService.initiateRecovery(id, email);
        return R.ok();
    }

    @Operation(summary = "获取所有角色(管理员)")
    @GetMapping("/manage/role")
    public R<List<String>> getRoles() {
        return R.ok(userService.getRoles());
    }

    @Operation(summary = "设置用户角色(管理员)")
    @PutMapping("/manage/role")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<Object> setRoles(@Valid @RequestBody SetRolesRequestVo vo) {
        userService.setRoles(vo);
        return R.ok();
    }
}
