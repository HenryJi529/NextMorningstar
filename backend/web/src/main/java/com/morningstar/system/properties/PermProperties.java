package com.morningstar.system.properties;

import com.morningstar.system.pojo.po.Permission;
import com.morningstar.system.pojo.po.Role;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "morningstar.perm")
@Data
public class PermProperties {
    private List<Role> roles;
    private List<Permission> permissions;
    private Map<String, List<String>> rolePermissions;
}
