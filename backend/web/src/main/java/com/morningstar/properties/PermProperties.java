package com.morningstar.properties;

import com.morningstar.common.pojo.po.Permission;
import com.morningstar.common.pojo.po.Role;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "morningstar.perm")
@Data
public class PermProperties {
    private List<Role> role;
    private List<Permission> permission;
    private Map<String, List<String>> rolePermission;
}
