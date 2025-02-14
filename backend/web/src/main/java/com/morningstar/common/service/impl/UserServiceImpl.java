package com.morningstar.common.service.impl;

import cn.hutool.core.lang.Validator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.morningstar.common.dao.mapper.RoleMapper;
import com.morningstar.common.dao.mapper.UserRoleMapper;
import com.morningstar.common.pojo.bo.*;
import com.morningstar.common.pojo.po.Role;
import com.morningstar.common.pojo.po.UserRole;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import com.morningstar.constant.RedisConstant;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.po.User;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.properties.JwtProperties;
import com.morningstar.properties.SiteProperties;
import com.morningstar.util.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;

    private final SiteProperties siteProperties;

    private final JwtUtil jwtUtil;

    private final FakerUtil fakerUtil;

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    private final EmailUtil emailUtil;

    private final JwtProperties jwtProperties;

    private final UserRoleMapper userRoleMapper;

    private final RoleMapper roleMapper;

    private void checkCode(String redisCode, String inputCode){
        if (StringUtils.isBlank(redisCode)){
            throw new BaseException(ResponseCode.CHECK_CODE_TIMEOUT);
        }
        if(!redisCode.equalsIgnoreCase(inputCode)) {
            throw new BaseException(ResponseCode.CHECK_CODE_ERROR);
        }
    }

    private void setLoginUserInRedis(LoginUser loginUser){
        redisTemplate.opsForValue().set(RedisConstant.LOGIN_PREFIX + loginUser.getUser().getId(), loginUser, jwtProperties.getTtl(), TimeUnit.MILLISECONDS);
    }

    private void updateLoginUserInRedis(UUID userId){
        String username = userMapper.selectById(userId).getUsername();
        LoginUser newLoginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
        setLoginUserInRedis(newLoginUser);
    }

    private void updateCurrentLoginUserInRedis() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser oldLoginUser = (LoginUser) authentication.getPrincipal();
        updateLoginUserInRedis(oldLoginUser.getUser().getId());
    }

    private void preCheckImageCaptcha(ImageCaptchaProtectedRequestVo vo){
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getSessionId());
        checkCode(redisCode, vo.getCode());
        redisTemplate.delete(RedisConstant.CHECK_PREFIX + vo.getSessionId());
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
        userPublicInfo.setIsOnline(redisTemplate.opsForValue().get(RedisConstant.HEARTBEAT_PREFIX + userPublicInfo.getId()) != null);
        return userPublicInfo;
    }

    /**
     * 获取EditableInfo
     */
    private UserEditableInfo getEditableInfo(UUID userId) {
        User dbUser = userMapper.selectById(userId);
        UserEditableInfo userEditableInfo = new UserEditableInfo();
        BeanUtils.copyProperties(dbUser, userEditableInfo);
        return userEditableInfo;
    }

    /**
     * 获取PrivateInfo
     */
    private UserPrivateInfo getPrivateInfo(LoginUser loginUser) {
        UserPrivateInfo userPrivateInfo = new UserPrivateInfo();
        BeanUtils.copyProperties(loginUser.getUser(), userPrivateInfo);
        userPrivateInfo.setPermissions(loginUser.getPermissions());

        return userPrivateInfo;
    }

    @Override
    public R<UserPrivateInfo> getPrivateInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        return R.ok(getPrivateInfo(loginUser));
    }

    @Override
    public R<LoginResponseVo> login(LoginRequestVo vo) {
        String username;
        if(Validator.isEmail(vo.getAccount())){
            User dbUser = userMapper.selectByEmail(vo.getAccount());
            if(dbUser == null){
                throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
            }
            username = dbUser.getUsername();
        }else {
            username = vo.getAccount();
        }
        // 检验验证码
        preCheckImageCaptcha(vo);

        // 验证密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, vo.getPassword());
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

        // 获取登录用户信息，并存入redis
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        setLoginUserInRedis(loginUser);

        // 组装登录成功数据
        LoginResponseVo respVo = new LoginResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
        return R.ok(respVo);
    }

    @Override
    public R<RegisterResponseVo> register(RegisterRequestVo vo) {
        // 检验验证码
        preCheckImageCaptcha(vo);

        // 判断用户名是否已经存在
        if(userMapper.selectByUsername(vo.getUsername()) != null){
            throw new BaseException(ResponseCode.ACCOUNT_EXISTS_ERROR);
        }

        // 密码加密
        vo.setPassword(passwordEncoder.encode(vo.getPassword()));

        // 插入数据库
        User dbUser = User.builder()
                .id(UUID.randomUUID())
                .build();
        BeanUtils.copyProperties(vo, dbUser);
        try{
            userMapper.insert(dbUser);
        }catch (DuplicateKeyException e){
            throw new BaseException(ResponseCode.ACCOUNT_INFO_ERROR);
        }

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());
        setLoginUserInRedis(loginUser);

        // 组装注册成功数据
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
    public R<UserEditableInfo> updateEmail(UpdateEmailRequestVo vo) {
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getEmail());
        checkCode(redisCode, vo.getCode());
        redisTemplate.delete(RedisConstant.CHECK_PREFIX + vo.getEmail());

        UUID userId = AuthUtil.getUserId();
        userMapper.updateById(User.builder().id(userId).email(vo.getEmail()).build());

        updateCurrentLoginUserInRedis();
        return R.ok(getEditableInfo(userId));
    }

    @Override
    public R<UserEditableInfo> updatePassword(UpdatePasswordRequestVo vo) {
        UUID userId = AuthUtil.getUserId();

        int count = userMapper.updateById(User.builder().id(userId).password(passwordEncoder.encode(vo.getNewPassword())).build());
        if(count != 1){
            throw new BaseException("更新密码失败");
        }
        updateCurrentLoginUserInRedis();
        return R.ok(getEditableInfo(userId));
    }

    @Override
    public R<UserEditableInfo> updateNickname(UpdateNicknameRequestVo vo) {
        UUID userId = AuthUtil.getUserId();

        int count = userMapper.updateById(User.builder().id(userId).nickname(vo.getNickname()).build());
        if(count != 1){
            throw new BaseException("更新昵称失败");
        }
        updateCurrentLoginUserInRedis();
        return R.ok(getEditableInfo(userId));
    }

    @Override
    public R<UserEditableInfo> updateAvatar(UpdateAvatarRequestVo vo) {
        UUID userId = AuthUtil.getUserId();

        int count = userMapper.updateById(User.builder().id(userId).avatar(vo.getAvatar()).build());
        if(count != 1){
            throw new BaseException("更新头像失败");
        }
        updateCurrentLoginUserInRedis();
        return R.ok(getEditableInfo(userId));
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

        // 更新数据库
        userMapper.updateById(User.builder().id(id).isLocked(isLocked).build());

        // 如果锁定，则删除redis中对应的loginUser
        if(isLocked){
            redisTemplate.delete(RedisConstant.LOGIN_PREFIX + id);
        }

        return R.ok();
    }

    @Override
    public R<String> refreshToken() {
        LoginUser loginUser = AuthUtil.getLoginUser();
        String token = jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), "");
        return R.ok(token);
    }

    private void sendRecoverEmail(UUID id, String email) {
        String token = RandomUtil.getString(20);
        redisTemplate.opsForValue().set(RedisConstant.RECOVER_PREFIX + id, token, 10, TimeUnit.MINUTES);
        String url = String.format("https://%s/auth/recover/complete?id=%s&token=%s", siteProperties.getDomain(), id, token);
        String content = String.format("链接: %s\n您可以通过该链接重新登入账号, 登入后请及时修改密码(临时令牌使用一次后就会失效[十分钟后也自动失效])。", url);
        emailUtil.sendSimpleEmail(email, "账号恢复", content);
    }

    @Override
    public R<Object> initiateRecovery(InitiateRecoveryRequestVo vo) {
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getEmail());
        checkCode(redisCode, vo.getCode());
        redisTemplate.delete(RedisConstant.CHECK_PREFIX + vo.getEmail());

        User dbUser = userMapper.selectByEmail(vo.getEmail());
        if(dbUser == null){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }

        UUID userId = dbUser.getId();
        sendRecoverEmail(userId, vo.getEmail());
        return R.ok();
    }

    @Override
    public R<Object> initiateRecovery(UUID id, String email) {
        User dbUser = userMapper.selectById(id);
        if(dbUser == null){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        sendRecoverEmail(id, email);
        return R.ok();
    }

    @Override
    public R<CompleteRecoveryResponseVo> completeRecovery(CompleteRecoveryRequestVo vo) {
        String redisToken = (String) redisTemplate.opsForValue().get(RedisConstant.RECOVER_PREFIX + vo.getId());
        if(StringUtils.isBlank(redisToken)){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        if(!redisToken.equals(vo.getToken())){
            throw new BaseException(ResponseCode.AUTHENTICATION_FAILED);
        }
        redisTemplate.delete(RedisConstant.RECOVER_PREFIX + vo.getId());
        User dbUser = userMapper.selectById(vo.getId());

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());
        setLoginUserInRedis(loginUser);

        // 组装恢复成功数据
        CompleteRecoveryResponseVo respVo = new CompleteRecoveryResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));

        return R.ok(respVo);
    }

    @Override
    public R<String> heartbeat() {
        UUID userId = AuthUtil.getUserId();
        redisTemplate.opsForValue().set(RedisConstant.HEARTBEAT_PREFIX + userId, System.currentTimeMillis(), 12, TimeUnit.SECONDS);
        return R.ok();
    }

    @Override
    public R<List<UserPrivateInfo>> getAllPrivateInfo() {
        List<User> users = userMapper.selectList(null);
        List<String> usernames = users.stream().map(User::getUsername).toList();
        List<LoginUser> loginUsers = usernames.stream().map(username -> (LoginUser) userDetailsService.loadUserByUsername(username)).toList();
        List<UserPrivateInfo> userPrivateInfos = loginUsers.stream().map(this::getPrivateInfo).toList();
        return R.ok(userPrivateInfos);
    }

    @Override
    public R<UserPrivateInfo> getPrivateInfo(UUID id) {
        User dbUser = userMapper.selectById(id);
        if(dbUser == null){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());

        return R.ok(getPrivateInfo(loginUser));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public R<Object> setRoles(SetRolesRequestVo vo) {
        User dbUser = userMapper.selectById(vo.getUserId());

        // 确保用户存在
        if(dbUser == null){
            throw new BaseException(ResponseCode.ACCOUNT_NOT_EXISTS);
        }

        // 删除该用户所有权限
        LambdaQueryWrapper<UserRole> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(UserRole::getUserId, dbUser.getId());
        userRoleMapper.delete(wrapper1);

        for(String roleTag: vo.getRoleTags()){
            // 确保权限存在
            LambdaQueryWrapper<Role> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Role::getTag, roleTag);
            if(!roleMapper.exists(wrapper2)){
                throw new BaseException(ResponseCode.ROLE_NOT_EXISTS);
            }
            // 重新添加权限
            UserRolePair userRolePair = UserRolePair.builder().username(dbUser.getUsername()).roleTag(roleTag).build();
            if(!userRoleMapper.exist(userRolePair)){
                userRoleMapper.insert(userRolePair);
            }
        }

        updateLoginUserInRedis(dbUser.getId());
        return R.ok();
    }

    @Override
    public R<List<String>> getRoles() {
        List<Role> roles = roleMapper.selectList(null);
        List<String> roleTags = roles.stream().map(Role::getTag).toList();
        return R.ok(roleTags);
    }
}
