package com.morningstar.practice.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pair {
    private Long personId;
    private Long movieId;
    private String type;
}
