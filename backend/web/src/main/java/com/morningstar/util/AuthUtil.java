package com.morningstar.util;

import com.morningstar.common.pojo.bo.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthUtil {
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (LoginUser) authentication.getPrincipal();
    }

    public static String getUsername() {
        return getLoginUser().getUser().getUsername();
    }

    public static UUID getUserId() {
        return getLoginUser().getUser().getId();
    }
}
