package com.morningstar.blog.pojo.bo;

import com.morningstar.blog.pojo.po.Tag;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDetail extends Tag {
    private Integer count;
}
