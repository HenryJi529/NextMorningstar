package com.morningstar.lab.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonMoviePair {
    private String personId;
    private String movieId;
    private String type;
}
