package com.morningstar.common.web.controller;

import com.morningstar.common.pojo.bo.UserEditableInfo;
import com.morningstar.common.pojo.bo.UserPrivateInfo;
import com.morningstar.common.pojo.bo.UserPublicInfo;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.common.service.UserService;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "用户相关接口定义")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @Operation(summary = "登入")
    @PostMapping("/auth/login")
    public R<LoginResponseVo> login(@Valid @RequestBody LoginRequestVo vo){
        return userService.login(vo);
    }

    @Operation(summary = "注册")
    @PostMapping("/auth/register")
    public R<RegisterResponseVo> register(@Valid @RequestBody RegisterRequestVo vo){
        return userService.register(vo);
    }

    @Operation(summary = "登出")
    @PostMapping("/auth/logout")
    public R<LogoutResponseVo> logout(){
        return userService.logout();
    }

    @Operation(summary = "公开信息精确查询(根据用username)")
    @Parameters({
            @Parameter(name = "username", description = "用户名", in = ParameterIn.PATH, required = true),
            @Parameter(name = HttpHeaders.AUTHORIZATION, description = "JWT", in = ParameterIn.HEADER),
    })
    @GetMapping("/info/public/exact/{username}")
    public R<UserPublicInfo> getPublicInfo(@PathVariable("username") String username){
        return userService.getPublicInfo(username);
    }

    @Operation(summary = "公开信息模糊查询")
    @GetMapping("/info/public/fuzzy")
    public R<PageResult<UserPublicInfo>> getPublicInfo(@Valid UserPublicInfoFuzzyPageQueryRequestVo vo){
        return userService.getPublicInfo(vo);
    }

    @Operation(summary = "私有信息查询(根据token)")
    @GetMapping("/info/private")
    public R<UserPrivateInfo> getPrivateInfo(){
        return userService.getPrivateInfo();
    }

    @Operation(summary = "私有信息查询(根据id)")
    @GetMapping("/info/private/{id}")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<UserPrivateInfo> getPrivateInfo(@PathVariable("id") UUID id) {
        return userService.getPrivateInfo(id);
    }

    @Operation(summary = "手机号更新")
    @PatchMapping("/info/phone")
    public R<UserEditableInfo> updatePhone(@Valid @RequestBody UpdatePhoneRequestVo vo){
        return userService.updatePhone(vo);
    }

    @Operation(summary = "邮箱更新")
    @PatchMapping("/info/email")
    public R<UserEditableInfo> updateEmail(@Valid @RequestBody UpdateEmailRequestVo vo){
        return userService.updateEmail(vo);
    }

    @Operation(summary = "密码更新")
    @PatchMapping("/info/password")
    public R<UserEditableInfo> updatePassword(@Valid @RequestBody UpdatePasswordRequestVo vo){
        return userService.updatePassword(vo);
    }

    @Operation(summary = "昵称更新")
    @PatchMapping("/info/nickname")
    public R<UserEditableInfo> updateNickname(@Valid @RequestBody UpdateNicknameRequestVo vo){
        return userService.updateNickname(vo);
    }

    @Operation(summary = "头像更新")
    @PatchMapping("/info/avatar")
    public R<UserEditableInfo> updateAvatar(@Valid @RequestBody UpdateAvatarRequestVo vo){
        return userService.updateAvatar(vo);
    }

    @Operation(summary = "昵称随机获取")
    @GetMapping("/random/nickname")
    public R<String> getRandomNickname(@RequestParam(value = "latitude", required = false) Double latitude, @RequestParam(value = "longitude", required = false) Double longitude){
        if(latitude == null || longitude == null){
            return userService.getRandomNickname();
        }else{
            return userService.getRandomNickname(latitude, longitude);
        }
    }

    @Operation(summary = "账号锁定与解锁")
    @GetMapping("/lock")
    @PreAuthorize("hasAuthority('sys:user:manage')")
    public R<Object> updateLockStatus(@RequestParam("id") UUID id, @RequestParam("isLocked") Boolean isLocked) {
        return userService.updateIsLocked(id, isLocked);
    }
}
