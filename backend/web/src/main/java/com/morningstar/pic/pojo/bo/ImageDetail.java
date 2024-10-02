package com.morningstar.pic.pojo.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageDetail extends Image {
    private String url;
}
