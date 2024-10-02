package com.morningstar.kill.service.impl;

import com.morningstar.kill.constant.RedisConstant;
import com.morningstar.kill.dao.mapper.UserMapper;
import com.morningstar.kill.pojo.bo.*;
import com.morningstar.kill.pojo.po.User;
import com.morningstar.kill.pojo.vo.req.*;
import com.morningstar.kill.pojo.vo.resp.*;
import com.morningstar.kill.util.AuthUtil;
import com.morningstar.kill.util.FakerUtil;
import com.morningstar.kill.util.JwtUtil;
import com.morningstar.kill.util.WebUtil;
import com.morningstar.kill.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FakerUtil fakerUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    private String getCheckCode(CaptchaProtectedRequestVo vo){
        String sessionId = vo.getSessionId();
        return (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + sessionId);
    }

    private ResponseCode preCheckCaptcha(CaptchaProtectedRequestVo vo){
        String redisCheckCode = getCheckCode(vo);
        if (StringUtils.isBlank(redisCheckCode)){
            return ResponseCode.CHECK_CODE_TIMEOUT;
        }
        if(!redisCheckCode.equalsIgnoreCase(vo.getCode())) {
            return ResponseCode.CHECK_CODE_ERROR;
        }
        return null;
    }

    @Override
    public R<UserPublicInfo> getPublicInfo(String username) {
        User dbUser = userMapper.selectByUsername(username);
        if (dbUser == null) {
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        UserPublicInfo userPublicInfo = new UserPublicInfo();
        BeanUtils.copyProperties(dbUser, userPublicInfo);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() != "anonymousUser"){
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            if(loginUser.getUsername().equals(username)){
                userPublicInfo.setIsSelf(true);
            }
        }
        return R.ok(userPublicInfo);
    }

    @Override
    public R<UserPrivateInfo> getPrivateInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        User dbUser = userMapper.selectByUsername(loginUser.getUsername());
        UserPrivateInfo userPrivateInfo = new UserPrivateInfo();
        BeanUtils.copyProperties(dbUser, userPrivateInfo);
        return R.ok(userPrivateInfo);
    }

    @Override
    public R<LoginResponseVo> login(LoginRequestVo vo) {
        // TODO: 支持多种方式登录
        // 检验验证码
        ResponseCode preCheckResponseCode = preCheckCaptcha(vo);
        if(preCheckResponseCode != null){
            return R.error(preCheckResponseCode);
        }

        // 验证密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(vo.getUsername(), vo.getPassword());
        Authentication authenticate;
        try{
            authenticate = authenticationManager.authenticate(authenticationToken);
        }catch (AuthenticationException e){
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        redisTemplate.opsForValue().set(RedisConstant.LOGIN_PREFIX + loginUser.getUser().getId(), loginUser);

        // 组装登录成功数据
        LoginResponseVo respVo = new LoginResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
        return R.ok(respVo);
    }

    @Override
    public R<RegisterResponseVo> register(RegisterRequestVo vo) {
        // 检验验证码
        ResponseCode preCheckResponseCode = preCheckCaptcha(vo);
        if(preCheckResponseCode != null){
            return R.error(preCheckResponseCode);
        }

        // 判断用户名是否已经存在
        if(userMapper.selectByUsername(vo.getUsername()) != null){
            return R.error(ResponseCode.ACCOUNT_EXISTS_ERROR);
        }

        // 密码加密
        vo.setPassword(passwordEncoder.encode(vo.getPassword()));

        // 插入数据库
        User dbUser = User.builder()
                .id(UUID.randomUUID())
                .createTime(new Timestamp(System.currentTimeMillis()))
                .updateTime(new Timestamp(System.currentTimeMillis()))
                .build();
        BeanUtils.copyProperties(vo, dbUser);
        try{
            userMapper.insert(dbUser);
        }catch (Exception e){
            return R.error(ResponseCode.ACCOUNT_INFO_ERROR);
        }

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());
        redisTemplate.opsForValue().set(RedisConstant.LOGIN_PREFIX + loginUser.getUser().getId(), loginUser);

        // 组装登录成功数据
        RegisterResponseVo respVo = new RegisterResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
        return R.ok(respVo);
    }

    @Override
    public R<LogoutResponseVo> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        UUID userid = loginUser.getUser().getId();
        redisTemplate.delete(RedisConstant.LOGIN_PREFIX + userid);
        return R.ok(new LogoutResponseVo(loginUser.getUsername()));
    }

    @Override
    public R<UserEditableInfo> updatePhone(UpdatePhoneRequestVo vo) {
        // TODO: 实现手机号更新
        return R.error(ResponseCode.NOT_IMPLEMENT);
    }

    @Override
    public R<UserEditableInfo> updateEmail(UpdateEmailRequestVo vo) {
        // TODO: 实现邮箱更新
        return R.error(ResponseCode.NOT_IMPLEMENT);
    }

    @Override
    public R<UserEditableInfo> updatePassword(UpdatePasswordRequestVo vo) {
        // 获取用户名
        String username = AuthUtil.getUsername();

        // 更新数据库中的密码字段
        userMapper.updatePasswordByUsername(passwordEncoder.encode(vo.getNewPassword()), username);

        return R.ok(userMapper.selectEditableInfoByUsername(username));
    }

    @Override
    public R<UserEditableInfo> updateNickname(UpdateNicknameRequestVo vo) {
        // 获取用户名
        String username = AuthUtil.getUsername();

        // 更新数据库中的昵称字段
        userMapper.updateNicknameByUsername(vo.getNickname(), username);

        return R.ok(userMapper.selectEditableInfoByUsername(username));
    }

    @Override
    public R<UserEditableInfo> updateAvatar(UpdateAvatarRequestVo vo) {
        // 获取用户名
        String username = AuthUtil.getUsername();

        // 更新数据库中的头像字段
        userMapper.updateAvatarByUsername(vo.getAvatar(), username);

        return R.ok(userMapper.selectEditableInfoByUsername(username));
    }

    @Override
    public R<String> getRandomNickname() {
        return R.ok(fakerUtil.getRandomNickname());
    }

    @Override
    public R<String> getRandomNickname(double latitude, double longitude) {
        return R.ok(fakerUtil.getRandomNickname(latitude, longitude));
    }

    @Override
    public void exportDailyStats(int dayNum, HttpServletResponse response) {
        // 获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        UUID userId = loginUser.getUser().getId();
        String username = loginUser.getUsername();

        // 读取当前用户的每日战绩
        List<UserDailyStats> userDailyStatsList = userMapper.selectRecentDailyStatsByUserId(dayNum, userId);

        // 输出为Excel
        String fileName = String.format("近%d日%s战绩信息.xlsx", dayNum, username);
        String sheetName = String.format("%s战绩信息(近%d日)", username, dayNum);
        WebUtil.renderExcel(userDailyStatsList, sheetName, fileName, UserDailyStats.class, response);
    }
}
