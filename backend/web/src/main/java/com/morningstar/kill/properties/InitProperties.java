package com.morningstar.kill.properties;

import com.morningstar.kill.pojo.po.Permission;
import com.morningstar.kill.pojo.po.Role;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kill.init")
@Data
public class InitProperties {
    private List<Role> role;
    private List<Permission> permission;
    private Map<String, List<String>> rolePermission;
}
