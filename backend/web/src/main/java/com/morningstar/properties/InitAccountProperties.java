package com.morningstar.properties;

import com.morningstar.common.pojo.bo.UserRolePair;
import com.morningstar.common.pojo.po.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "morningstar.init.account")
@Data
public class InitAccountProperties {
    private List<User> user;

    private List<UserRolePair> userRole;
}