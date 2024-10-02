package com.morningstar.pic.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usage {
    private Long usedStorage;
    private Integer imageCount;
    private Integer dayCount;
}
