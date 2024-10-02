package com.morningstar.pic.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("pic_config")
@Schema(description = "图床配置对象")
public class Config {
    @Schema(description = "用户id")
    @TableId("user_id")
    private UUID userId;

    private String secretKey;

    private String githubPat;

    private Float compressionQuality;
}