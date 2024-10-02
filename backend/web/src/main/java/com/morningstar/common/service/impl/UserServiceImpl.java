package com.morningstar.common.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.morningstar.common.dao.mapper.PermissionMapper;
import com.morningstar.common.pojo.bo.LoginUser;
import com.morningstar.common.pojo.bo.UserEditableInfo;
import com.morningstar.common.pojo.bo.UserPrivateInfo;
import com.morningstar.common.pojo.bo.UserPublicInfo;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import com.morningstar.constant.RedisConstant;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.po.User;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.util.AuthUtil;
import com.morningstar.util.FakerUtil;
import com.morningstar.util.JwtUtil;
import com.morningstar.common.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final FakerUtil fakerUtil;

    private final UserMapper userMapper;

    private final PermissionMapper permissionMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

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
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        return R.ok(getPublicInfo(dbUser));
    }

    @Override
    public R<PageResult<UserPublicInfo>> getPublicInfo(UserPublicInfoFuzzyPageQueryRequestVo vo) {
        try(Page<Object> ignored = PageHelper.startPage(vo.getPage(), vo.getPageSize())){
            List<User> userList = userMapper.selectByFuzzyValue(vo.getFuzzyValue());

            PageResult<UserPublicInfo> pageResult = new PageResult<>(new PageInfo<>(
                    userList.stream().map(this::getPublicInfo).collect(Collectors.toList())
            ));
            pageResult.fixPageInfo(new PageInfo<>(userList));
            return R.ok(pageResult);
        }
    }

    /**
     * 从user中获取public字段，并设置isSelf(判断访问者是否为该user)
     */
    private UserPublicInfo getPublicInfo(User dbUser){
        UserPublicInfo userPublicInfo = new UserPublicInfo();
        BeanUtils.copyProperties(dbUser, userPublicInfo);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() != "anonymousUser"){
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            if(loginUser.getUsername().equals(dbUser.getUsername())){
                userPublicInfo.setIsSelf(true);
            }
        }
        return userPublicInfo;
    }

    /**
     * 获取EditableInfo
     */
    private UserEditableInfo getEditableInfo(String username) {
        User dbUser = userMapper.selectByUsername(username);
        UserEditableInfo userEditableInfo = new UserEditableInfo();
        BeanUtils.copyProperties(dbUser, userEditableInfo);
        return userEditableInfo;
    }

    @Override
    public R<UserPrivateInfo> getPrivateInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        UserPrivateInfo userPrivateInfo = new UserPrivateInfo();
        BeanUtils.copyProperties(loginUser.getUser(), userPrivateInfo);
        userPrivateInfo.setPermissions(loginUser.getPermissions());

        return R.ok(userPrivateInfo);
    }

    @Override
    public R<LoginResponseVo> login(LoginRequestVo vo) {
        // TODO: 支持多种方式登录
        // 检验验证码
        ResponseCode preCheckResponseCode = preCheckCaptcha(vo);
        if(preCheckResponseCode != null){
            throw new BaseException(preCheckResponseCode);
        }

        // 验证密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(vo.getUsername(), vo.getPassword());
        Authentication authenticate;
        try{
            authenticate = authenticationManager.authenticate(authenticationToken);
        }catch (AuthenticationException e){
            if(e instanceof BadCredentialsException){
                throw new BaseException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
            }else if(e instanceof LockedException){
                throw new BaseException(ResponseCode.ACCOUNT_LOCKED_ERROR);
            }else {
                throw new BaseException(ResponseCode.AUTHENTICATION_FAILED);
            }
        }

        // 获取登录用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        // 将用户信息存入redis
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
            throw new BaseException(preCheckResponseCode);
        }

        // 判断用户名是否已经存在
        if(userMapper.selectByUsername(vo.getUsername()) != null){
            throw new BaseException(ResponseCode.ACCOUNT_EXISTS_ERROR);
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
        }catch (DuplicateKeyException e){
            throw new BaseException(ResponseCode.ACCOUNT_INFO_ERROR);
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
        throw new BaseException(ResponseCode.NOT_IMPLEMENT);
    }

    @Override
    public R<UserEditableInfo> updateEmail(UpdateEmailRequestVo vo) {
        // TODO: 实现邮箱更新
        throw new BaseException(ResponseCode.NOT_IMPLEMENT);
    }

    @Override
    public R<UserEditableInfo> updatePassword(UpdatePasswordRequestVo vo) {
        UUID userId = AuthUtil.getUserId();
        String username = AuthUtil.getUsername();

        int count = userMapper.updateById(User.builder().id(userId).password(passwordEncoder.encode(vo.getNewPassword())).build());
        if(count != 1){
            throw new BaseException("更新密码失败");
        }
        return R.ok(getEditableInfo(username));
    }

    @Override
    public R<UserEditableInfo> updateNickname(UpdateNicknameRequestVo vo) {
        UUID userId = AuthUtil.getUserId();
        String username = AuthUtil.getUsername();

        int count = userMapper.updateById(User.builder().id(userId).nickname(vo.getNickname()).build());
        if(count != 1){
            throw new BaseException("更新昵称失败");
        }
        return R.ok(getEditableInfo(username));
    }

    @Override
    public R<UserEditableInfo> updateAvatar(UpdateAvatarRequestVo vo) {
        UUID userId = AuthUtil.getUserId();
        String username = AuthUtil.getUsername();

        int count = userMapper.updateById(User.builder().id(userId).avatar(vo.getAvatar()).build());
        if(count != 1){
            throw new BaseException("更新头像失败");
        }
        return R.ok(getEditableInfo(username));
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
    public R<Object> updateIsLocked(UUID id, Boolean isLocked) {
        if(userMapper.selectById(id) == null){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        // 如果锁定，则删除redis中对应的loginUser
        if(isLocked){
            redisTemplate.delete(RedisConstant.LOGIN_PREFIX + id);
        }

        // 更新数据库
        int count = userMapper.updateById(User.builder().id(id).isLocked(isLocked).build());
        if(count != 1) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        return R.ok();
    }

    @Override
    public R<UserPrivateInfo> getPrivateInfo(UUID id) {
        User dbUser = userMapper.selectById(id);
        if(dbUser == null){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        Set<String> permissions = permissionMapper.selectTagByUserId(dbUser.getId());

        UserPrivateInfo userPrivateInfo = new UserPrivateInfo();
        BeanUtils.copyProperties(dbUser, userPrivateInfo);
        userPrivateInfo.setPermissions(permissions);

        return R.ok(userPrivateInfo);
    }
}
