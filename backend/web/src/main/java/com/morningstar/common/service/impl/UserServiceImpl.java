package com.morningstar.common.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.morningstar.common.dao.mapper.RoleMapper;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.dao.mapper.UserRoleMapper;
import com.morningstar.common.pojo.bo.*;
import com.morningstar.common.pojo.po.Role;
import com.morningstar.common.pojo.po.User;
import com.morningstar.common.pojo.po.UserRole;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.common.service.UserService;
import com.morningstar.constant.GithubConstant;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.RedisConstant;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.properties.JwtProperties;
import com.morningstar.properties.OAuth2Properties;
import com.morningstar.properties.SiteProperties;
import com.morningstar.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    private final OAuth2Properties oAuth2Properties;

    private final RestTemplate restTemplate;

    private final GithubUtil githubUtil;

    private final QiniuUtil qiniuUtil;

    private void checkCode(String redisCode, String inputCode) {
        if (StringUtils.isBlank(redisCode)) {
            throw new BaseException(ResponseCode.CHECK_CODE_TIMEOUT);
        }
        if (!redisCode.equalsIgnoreCase(inputCode)) {
            throw new BaseException(ResponseCode.CHECK_CODE_ERROR);
        }
    }

    private void setLoginUserInRedis(LoginUser loginUser) {
        redisTemplate.opsForValue().set(RedisConstant.LOGIN_PREFIX + loginUser.getUser().getId(), loginUser, jwtProperties.getTtl(), TimeUnit.MILLISECONDS);
    }

    private void updateLoginUserInRedis(UUID userId) {
        String username = userMapper.selectById(userId).getUsername();
        LoginUser newLoginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
        setLoginUserInRedis(newLoginUser);
    }

    private void updateCurrentLoginUserInRedis() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser oldLoginUser = (LoginUser) authentication.getPrincipal();
        updateLoginUserInRedis(oldLoginUser.getUser().getId());
    }

    private void preCheckImageCaptcha(ImageCaptchaProtectedRequestVo vo) {
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getSessionId());
        checkCode(redisCode, vo.getCode());
        redisTemplate.delete(RedisConstant.CHECK_PREFIX + vo.getSessionId());
    }

    @Override
    public UserPublicInfo getPublicInfo(String username) {
        User dbUser = userMapper.selectByUsername(username);
        if (dbUser == null) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        return getPublicInfo(dbUser);
    }

    @Override
    public PageResult<UserPublicInfo> getPublicInfo(UserPublicInfoFuzzyPageQueryRequestVo vo) {
        try (Page<Object> ignored = PageHelper.startPage(vo.getPage(), vo.getPageSize())) {
            List<User> userList = userMapper.selectByFuzzyValue(vo.getFuzzyValue());

            PageResult<UserPublicInfo> pageResult = new PageResult<>(new PageInfo<>(
                    userList.stream().map(this::getPublicInfo).collect(Collectors.toList())
            ));
            pageResult.fixPageInfo(new PageInfo<>(userList));
            return pageResult;
        }
    }

    /**
     * 从user中获取public字段，并设置isSelf(判断访问者是否为该user)
     */
    private UserPublicInfo getPublicInfo(User dbUser) {
        UserPublicInfo userPublicInfo = new UserPublicInfo();
        BeanUtils.copyProperties(dbUser, userPublicInfo);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() != "anonymousUser") {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            if (loginUser.getUsername().equals(dbUser.getUsername())) {
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
    public UserPrivateInfo getPrivateInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        return getPrivateInfo(loginUser);
    }

    @Override
    public LoginResponseVo login(LoginRequestVo vo) {
        String username;
        if (Validator.isEmail(vo.getAccount())) {
            User dbUser = userMapper.selectByEmail(vo.getAccount());
            if (dbUser == null) {
                throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
            }
            username = dbUser.getUsername();
        } else {
            username = vo.getAccount();
        }
        // 检验验证码
        preCheckImageCaptcha(vo);

        // 验证密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, vo.getPassword());
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new BaseException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
            } else if (e instanceof LockedException) {
                throw new BaseException(ResponseCode.ACCOUNT_LOCKED);
            } else {
                throw new BaseException(ResponseCode.AUTHENTICATION_FAILED);
            }
        }

        // 获取登录用户信息，并存入redis
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        setLoginUserInRedis(loginUser);

        // 组装登录成功数据
        return new LoginResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
    }

    @Override
    public RegisterResponseVo register(RegisterRequestVo vo) {
        // 检验验证码
        preCheckImageCaptcha(vo);

        // 判断用户名是否已经存在
        if (userMapper.selectByUsername(vo.getUsername()) != null) {
            throw new BaseException(ResponseCode.USERNAME_ALREADY_EXISTS);
        }

        // 判断用户名是否合法
        if (vo.getUsername().startsWith(GithubConstant.GITHUB_USER_PREFIX)) {
            throw new BaseException(ResponseCode.USERNAME_INVALID);
        }

        // 密码加密
        vo.setPassword(passwordEncoder.encode(vo.getPassword()));

        // 插入数据库
        User dbUser = User.builder()
                .id(UUID.randomUUID())
                .build();
        BeanUtils.copyProperties(vo, dbUser);
        try {
            userMapper.insert(dbUser);
        } catch (DuplicateKeyException e) {
            throw new BaseException(ResponseCode.ACCOUNT_INFO_ERROR);
        }

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());
        setLoginUserInRedis(loginUser);

        // 组装注册成功数据
        return new RegisterResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
    }

    @Override
    public LogoutResponseVo logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        UUID userid = loginUser.getUser().getId();
        redisTemplate.delete(RedisConstant.LOGIN_PREFIX + userid);
        return new LogoutResponseVo(loginUser.getUsername());
    }

    private void checkEmailNotExist(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BaseException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public UserEditableInfo updateEmail(UpdateEmailRequestVo vo) {
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getEmail());
        checkCode(redisCode, vo.getCode());
        redisTemplate.delete(RedisConstant.CHECK_PREFIX + vo.getEmail());

        // 判断邮箱是否存在
        checkEmailNotExist(vo.getEmail());

        UUID userId = AuthUtil.getUserId();
        userMapper.updateById(User.builder().id(userId).email(vo.getEmail()).build());

        updateCurrentLoginUserInRedis();
        return getEditableInfo(userId);
    }

    @Override
    public UserEditableInfo updatePassword(UpdatePasswordRequestVo vo) {
        UUID userId = AuthUtil.getUserId();

        int count = userMapper.updateById(User.builder().id(userId).password(passwordEncoder.encode(vo.getNewPassword())).build());
        if (count != 1) {
            throw new BaseException("更新密码失败");
        }
        updateCurrentLoginUserInRedis();
        return getEditableInfo(userId);
    }

    @Override
    public UserEditableInfo updateNickname(UpdateNicknameRequestVo vo) {
        UUID userId = AuthUtil.getUserId();

        int count = userMapper.updateById(User.builder().id(userId).nickname(vo.getNickname()).build());
        if (count != 1) {
            throw new BaseException("更新昵称失败");
        }
        updateCurrentLoginUserInRedis();
        return getEditableInfo(userId);
    }

    @Override
    public UserEditableInfo updateAvatar(UpdateAvatarRequestVo vo) {
        UUID userId = AuthUtil.getUserId();

        int count = userMapper.updateById(User.builder().id(userId).avatar(vo.getAvatar()).build());
        if (count != 1) {
            throw new BaseException("更新头像失败");
        }
        updateCurrentLoginUserInRedis();
        return getEditableInfo(userId);
    }

    @Override
    public String getRandomNickname() {
        return fakerUtil.getRandomNickname();
    }

    @Override
    public String getRandomNickname(double latitude, double longitude) {
        return fakerUtil.getRandomNickname(latitude, longitude);
    }

    @Override
    public void updateIsLocked(UUID id, Boolean isLocked) {
        if (userMapper.selectById(id) == null) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }

        // 更新数据库
        userMapper.updateById(User.builder().id(id).isLocked(isLocked).build());

        // 如果锁定，则删除redis中对应的loginUser
        if (isLocked) {
            redisTemplate.delete(RedisConstant.LOGIN_PREFIX + id);
        }
    }

    @Override
    public String refreshToken() {
        LoginUser loginUser = AuthUtil.getLoginUser();
        return jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), "");
    }

    private void sendRecoverEmail(UUID id, String email) {
        String token = RandomUtil.getString(20);
        redisTemplate.opsForValue().set(RedisConstant.RECOVER_PREFIX + id, token, 10, TimeUnit.MINUTES);
        String url = String.format("https://%s/auth/recover/complete?id=%s&token=%s", siteProperties.getDomain(), id, token);
        String content = String.format("链接: %s\n您可以通过该链接重新登入账号, 登入后请及时修改密码(临时令牌使用一次后就会失效[十分钟后也自动失效])。", url);
        emailUtil.sendSimpleEmail(email, "账号恢复", content);
    }

    @Override
    public void initiateRecovery(InitiateRecoveryRequestVo vo) {
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getEmail());
        checkCode(redisCode, vo.getCode());
        redisTemplate.delete(RedisConstant.CHECK_PREFIX + vo.getEmail());

        User dbUser = userMapper.selectByEmail(vo.getEmail());
        if (dbUser == null) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }

        UUID userId = dbUser.getId();
        sendRecoverEmail(userId, vo.getEmail());
    }

    @Override
    public void initiateRecovery(UUID id, String email) {
        User dbUser = userMapper.selectById(id);
        if (dbUser == null) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        sendRecoverEmail(id, email);
    }

    @Override
    public CompleteRecoveryResponseVo completeRecovery(CompleteRecoveryRequestVo vo) {
        String redisToken = (String) redisTemplate.opsForValue().get(RedisConstant.RECOVER_PREFIX + vo.getId());
        if (StringUtils.isBlank(redisToken)) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        if (!redisToken.equals(vo.getToken())) {
            throw new BaseException(ResponseCode.AUTHENTICATION_FAILED);
        }
        redisTemplate.delete(RedisConstant.RECOVER_PREFIX + vo.getId());
        User dbUser = userMapper.selectById(vo.getId());

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());
        setLoginUserInRedis(loginUser);

        // 组装恢复成功数据
        return new CompleteRecoveryResponseVo(loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
    }

    @Override
    public void heartbeat() {
        UUID userId = AuthUtil.getUserId();
        redisTemplate.opsForValue().set(RedisConstant.HEARTBEAT_PREFIX + userId, System.currentTimeMillis(), 12, TimeUnit.SECONDS);
    }

    @Override
    public List<UserPrivateInfo> getAllPrivateInfo() {
        List<User> users = userMapper.selectList(null);
        List<String> usernames = users.stream().map(User::getUsername).toList();
        List<LoginUser> loginUsers = usernames.stream().map(username -> (LoginUser) userDetailsService.loadUserByUsername(username)).toList();
        return loginUsers.stream().map(this::getPrivateInfo).toList();
    }

    @Override
    public UserPrivateInfo getPrivateInfo(UUID id) {
        User dbUser = userMapper.selectById(id);
        if (dbUser == null) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());

        return getPrivateInfo(loginUser);
    }

    @Override
    public List<String> getRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return roles.stream().map(Role::getTag).toList();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void setRoles(SetRolesRequestVo vo) {
        User dbUser = userMapper.selectById(vo.getUserId());

        // 确保用户存在
        if (dbUser == null) {
            throw new BaseException(ResponseCode.ACCOUNT_NOT_FOUND);
        }

        // 删除该用户所有权限
        LambdaQueryWrapper<UserRole> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(UserRole::getUserId, dbUser.getId());
        userRoleMapper.delete(wrapper1);

        for (String roleTag : vo.getRoleTags()) {
            // 确保权限存在
            LambdaQueryWrapper<Role> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Role::getTag, roleTag);
            if (!roleMapper.exists(wrapper2)) {
                throw new BaseException(ResponseCode.ROLE_NOT_EXISTS);
            }
            // 重新添加权限
            UserRolePair userRolePair = UserRolePair.builder().username(dbUser.getUsername()).roleTag(roleTag).build();
            if (!userRoleMapper.exist(userRolePair)) {
                userRoleMapper.insert(userRolePair);
            }
        }

        updateLoginUserInRedis(dbUser.getId());
    }

    @Override
    public OAuthResponseVo githubOAuthCallback(String code) {
        String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", oAuth2Properties.getGithubClientId());
        requestBody.add("client_secret", oAuth2Properties.getGithubClientSecret());
        requestBody.add("code", code);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                GITHUB_TOKEN_URL,
                requestEntity,
                String.class
        );
        JSONObject jsonObject = JSONUtil.parseObj(response.getBody());
        if (jsonObject.containsKey("error")) {
            throw new BaseException(ResponseCode.AUTHENTICATION_FAILED);
        }
        String githubToken = JSONUtil.parseObj(response.getBody()).getStr("access_token");

        GithubUtil.GithubAccount githubAccount = githubUtil.getUserAsAccount(githubToken);
        String username = GithubConstant.GITHUB_USER_PREFIX + githubAccount.getId();

        User dbUser = userMapper.selectByUsername(username);
        if (dbUser == null) {
            /* 如果用户不存在就注册 */

            // 判断邮箱是否存在
            checkEmailNotExist(githubAccount.getEmail());

            // 图片保存到七牛云
            String avatar = new Date().getTime() + ".jpeg";
            byte[] data = Objects.requireNonNull(restTemplate.getForObject(githubAccount.getAvatarUrl(), byte[].class));
            if (!qiniuUtil.uploadFile("morningstar/user/avatar/" + avatar, data)) {
                avatar = null;
            }

            dbUser = User.builder()
                    .id(UUID.randomUUID())
                    .username(username)
                    .password(passwordEncoder.encode(RandomUtil.getString(8)))
                    .email(githubAccount.getEmail())
                    .avatar(avatar)
                    .nickname(fakerUtil.getRandomNickname())
                    .build();
            userMapper.insert(dbUser);
        }

        // 获取登录用户信息，存入redis
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(dbUser.getUsername());
        setLoginUserInRedis(loginUser);

        // 组装注册成功数据
        return new OAuthResponseVo(loginUser.getUser().getId(), loginUser.getUsername(), jwtUtil.create(loginUser.getUser().getId(), loginUser.getUsername(), ""));
    }
}
