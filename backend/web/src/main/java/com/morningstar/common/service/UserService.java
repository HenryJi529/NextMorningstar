package com.morningstar.common.service;

import com.morningstar.common.pojo.bo.UserEditableInfo;
import com.morningstar.common.pojo.bo.UserPrivateInfo;
import com.morningstar.common.pojo.bo.UserPublicInfo;
import com.morningstar.common.pojo.vo.req.*;
import com.morningstar.common.pojo.vo.resp.*;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;

import java.util.UUID;


public interface UserService {
    R<UserPublicInfo> getPublicInfo(String username);

    R<PageResult<UserPublicInfo>> getPublicInfo(UserPublicInfoFuzzyPageQueryRequestVo vo);

    R<UserPrivateInfo> getPrivateInfo(UUID id);

    R<UserPrivateInfo> getPrivateInfo();

    R<LoginResponseVo> login(LoginRequestVo vo);

    R<RegisterResponseVo> register(RegisterRequestVo vo);

    R<LogoutResponseVo> logout();

    R<UserEditableInfo> updatePhone(UpdatePhoneRequestVo vo);

    R<UserEditableInfo> updateEmail(UpdateEmailRequestVo vo);

    R<UserEditableInfo> updatePassword(UpdatePasswordRequestVo vo);

    R<UserEditableInfo> updateNickname(UpdateNicknameRequestVo vo);

    R<UserEditableInfo> updateAvatar(UpdateAvatarRequestVo vo);

    R<String> getRandomNickname();

    R<String> getRandomNickname(double latitude, double longitude);

    R<Object> updateIsLocked(UUID id, Boolean isLocked);
}
