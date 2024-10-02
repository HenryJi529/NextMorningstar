package com.morningstar.common.pojo.vo.req;

import com.morningstar.validation.constraint.Set;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Schema(description = "角色设置请求对象")
@ToString(callSuper = true)
@Data
public class SetRolesRequestVo {
    @Set
    @NotNull
    private List<String> roleTags;

    @NotNull
    private UUID userId;
}
