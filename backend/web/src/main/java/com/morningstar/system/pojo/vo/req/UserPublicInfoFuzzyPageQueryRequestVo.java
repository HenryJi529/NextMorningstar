package com.morningstar.system.pojo.vo.req;

import lombok.Data;

@Data
public class UserPublicInfoFuzzyPageQueryRequestVo {
    private String fuzzyValue;
    private int page;
    private int pageSize;
}
