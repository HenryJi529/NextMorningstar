package com.morningstar.common.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Schema(description = "用户公开信息对象")
public class UserPublicInfo {
    @Schema(description = "是否为本人")
    private Boolean isSelf = false;

    @Schema(description = "用户ID")
    private UUID id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "是否锁定")
    private Boolean isLocked;

    @Schema(description = "是否在线")
    private Boolean isOnline;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
