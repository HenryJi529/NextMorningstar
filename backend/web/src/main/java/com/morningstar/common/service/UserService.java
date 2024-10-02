package com.morningstar.common.service;

import com.morningstar.common.pojo.bo.UserEditableInfo;
import com.morningstar.common.pojo.bo.UserPrivateInfo;
import com.morningstar.common.pojo.bo.UserPublicInfo;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.constant.PageResult;

import java.util.List;
import java.util.UUID;


public interface UserService {
    UserPublicInfo getPublicInfo(String username);

    PageResult<UserPublicInfo> getPublicInfo(UserPublicInfoFuzzyPageQueryRequestVo vo);

    UserPrivateInfo getPrivateInfo();

    LoginResponseVo login(LoginRequestVo vo);

    RegisterResponseVo register(RegisterRequestVo vo);

    LogoutResponseVo logout();

    UserEditableInfo updateEmail(UpdateEmailRequestVo vo);

    UserEditableInfo updatePassword(UpdatePasswordRequestVo vo);

    UserEditableInfo updateNickname(UpdateNicknameRequestVo vo);

    UserEditableInfo updateAvatar(UpdateAvatarRequestVo vo);

    String getRandomNickname();

    String getRandomNickname(double latitude, double longitude);

    void updateIsLocked(UUID id, Boolean isLocked);

    String refreshToken();

    void initiateRecovery(InitiateRecoveryRequestVo vo);

    void initiateRecovery(UUID id, String email);

    CompleteRecoveryResponseVo completeRecovery(CompleteRecoveryRequestVo vo);

    void heartbeat();

    List<UserPrivateInfo> getAllPrivateInfo();

    UserPrivateInfo getPrivateInfo(UUID id);

    List<String> getRoles();

    void setRoles(SetRolesRequestVo vo);

    OAuthResponseVo githubOAuthCallback(String code);
}
