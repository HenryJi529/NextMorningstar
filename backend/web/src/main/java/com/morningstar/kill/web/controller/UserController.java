package com.morningstar.kill.web.controller;

import com.morningstar.kill.pojo.bo.*;
import com.morningstar.kill.pojo.vo.req.*;
import com.morningstar.kill.pojo.vo.resp.LoginResponseVo;
import com.morningstar.kill.pojo.vo.resp.LogoutResponseVo;
import com.morningstar.kill.pojo.vo.resp.R;
import com.morningstar.kill.pojo.vo.resp.RegisterResponseVo;
import com.morningstar.kill.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "用户认证相关接口定义")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户公开信息查询
     */
    @Operation(summary = "用户公开信息查询")
    @Parameters({
            @Parameter(name = "username", description = "用户名", in = ParameterIn.PATH, required = true),
            @Parameter(name = HttpHeaders.AUTHORIZATION, description = "JWT", in = ParameterIn.HEADER),
    })
    @GetMapping("/info/{username}")
    public R<UserPublicInfo> getPublicInfo(@PathVariable("username") String username){
        return userService.getPublicInfo(username);
    }

    /**
     * 用户私有信息查询
     */
    @Operation(summary = "用户私有信息查询")
    @GetMapping("/info")
    public R<UserPrivateInfo> getPrivateInfo(){
        return userService.getPrivateInfo();
    }

    /**
     * 用户登入
     */
    @Operation(summary = "用户登入")
    @PostMapping("/login")
    public R<LoginResponseVo> login(@Valid @RequestBody LoginRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return R.error(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return this.userService.login(vo);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<RegisterResponseVo> register(@Valid @RequestBody RegisterRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return R.error(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return this.userService.register(vo);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<LogoutResponseVo> logout(){
        return userService.logout();
    }

    /**
     * 用户手机号更新
     */
    @Operation(summary = "用户手机号更新")
    @PatchMapping("/update/phone")
    public R<UserEditableInfo> updatePhone(@Valid @RequestBody UpdatePhoneRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return R.error(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return userService.updatePhone(vo);
    }

    /**
     * 用户邮箱更新
     */
    @Operation(summary = "用户邮箱更新")
    @PatchMapping("/update/email")
    public R<UserEditableInfo> updateEmail(@Valid @RequestBody UpdateEmailRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return R.error(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return userService.updateEmail(vo);
    }

    /**
     * 用户密码更新
     */
    @Operation(summary = "用户密码更新")
    @PatchMapping("/update/password")
    public R<UserEditableInfo> updatePassword(@Valid @RequestBody UpdatePasswordRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return R.error(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return userService.updatePassword(vo);
    }

    /**
     * 用户昵称更新
     */
    @Operation(summary = "用户昵称更新")
    @PatchMapping("/update/nickname")
    public R<UserEditableInfo> updateNickname(@Valid @RequestBody UpdateNicknameRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return R.error(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return userService.updateNickname(vo);
    }

    /**
     * 用户头像更新
     */
    @Operation(summary = "用户头像更新")
    @PatchMapping("/update/avatar")
    public R<UserEditableInfo> updateAvatar(@Valid @RequestBody UpdateAvatarRequestVo vo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return R.error(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; ")));
        }
        return userService.updateAvatar(vo);
    }

    /**
     * 用户昵称随机获取
     */
    @Operation(summary = "用户昵称随机获取")
    @GetMapping("/random/nickname")
    public R<String> getRandomNickname(@RequestParam(value = "latitude", required = false) Double latitude, @RequestParam(value = "longitude", required = false) Double longitude){
        if(latitude == null || longitude == null){
            return userService.getRandomNickname();
        }else{
            return userService.getRandomNickname(latitude, longitude);
        }
    }

    /**
     * 每日战绩导出
     */
    @Parameter(name = "dayNum", description = "天数", in = ParameterIn.QUERY)
    @Operation(summary = "每日战绩导出", description = "导出近{dayNum}天的各游戏模式胜率")
    @GetMapping("/game/stats/daily")
    public void exportDailyStats(@RequestParam(name = "dayNum", required = false, defaultValue = "10") int dayNum, HttpServletResponse response){
        userService.exportDailyStats(dayNum, response);
    }

    /**
     * 赛季表现获取
     */
    @Operation(summary = "赛季战绩获取")
    @GetMapping("/game/stats/season")
    public R<UserSeasonStats> getSeasonStats(){
        // TODO: 实现功能
        return R.ok(new UserSeasonStats(90.0, 80.0, 70.52122, 60.4567, 90.21, 43.897));
    }

    /**
     * 赛季排名获取
     */
    @Operation(summary = "赛季排名获取")
    @GetMapping("/game/rank/season")
    public R<UserSeasonRank> getSeasonRank(){
        // TODO: 实现功能
        return R.ok(new UserSeasonRank(1, 43, 123, 5445, 65423, 546122));
    }

    /**
     * 感谢信息获取
     */
    @Operation(summary = "感谢信息获取")
    @GetMapping("/thank")
    @PreAuthorize("hasAuthority('thank')")
    public R<String> thank(){
        return R.ok();
    }
}
