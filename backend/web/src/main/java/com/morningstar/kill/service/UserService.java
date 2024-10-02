package com.morningstar.kill.service;

import com.morningstar.kill.pojo.bo.UserEditableInfo;
import com.morningstar.kill.pojo.bo.UserPrivateInfo;
import com.morningstar.kill.pojo.bo.UserPublicInfo;
import com.morningstar.kill.pojo.vo.req.*;
import com.morningstar.kill.pojo.vo.resp.LoginResponseVo;
import com.morningstar.kill.pojo.vo.resp.LogoutResponseVo;
import com.morningstar.kill.pojo.vo.resp.R;
import com.morningstar.kill.pojo.vo.resp.RegisterResponseVo;
import jakarta.servlet.http.HttpServletResponse;


public interface UserService {
    R<UserPublicInfo> getPublicInfo(String username);

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

    void exportDailyStats(int dayNum, HttpServletResponse response);
}
