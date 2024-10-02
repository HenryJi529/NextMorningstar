package com.morningstar.love.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "爱の相册数据对象")
public class LovePhotoData {
    @Schema(description = "最大块级别")
    private Integer maxBlockLevel;
    @Schema(description = "最小块宽度")
    private Integer minBlockWidth;
    @Schema(description = "最小块高度")
    private Integer minBlockHeight;

    @Schema(description = "图片名")
    private String photoName;
    @Schema(description = "base64数据")
    private String base64String;
    @Schema(description = "填充色数组")
    private String[][] fills;
}
