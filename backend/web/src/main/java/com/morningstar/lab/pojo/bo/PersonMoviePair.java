package com.morningstar.lab.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonMoviePair {
    private Long personId;
    private Long movieId;
    private String type;
}
