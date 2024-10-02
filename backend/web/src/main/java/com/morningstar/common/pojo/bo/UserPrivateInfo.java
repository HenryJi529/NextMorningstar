package com.morningstar.common.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Schema(description = "用户私有信息对象")
public class UserPrivateInfo {
    @Schema(description = "用户ID")
    private UUID id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "是否锁定")
    private Boolean isLocked;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "权限")
    private Set<String> permissions; // NOTE: 包含角色

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
