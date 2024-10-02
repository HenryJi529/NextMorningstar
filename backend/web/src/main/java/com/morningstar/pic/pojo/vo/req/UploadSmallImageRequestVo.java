package com.morningstar.pic.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "上传小图请求对象")
public class UploadSmallImageRequestVo {
    @NotBlank
    private String filename;

    @NotBlank
    private String content;
}
