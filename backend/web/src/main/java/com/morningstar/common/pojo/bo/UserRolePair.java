package com.morningstar.common.pojo.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRolePair {
    private String username;

    private String roleTag;
}
