package com.morningstar.common.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPublicInfoFuzzyPageQueryRequestVo implements Serializable {
    private String fuzzyValue;
    private int page;
    private int pageSize;
}
