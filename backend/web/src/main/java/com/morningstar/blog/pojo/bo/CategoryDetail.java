package com.morningstar.blog.pojo.bo;

import com.morningstar.blog.pojo.po.Category;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetail extends Category {
    private Integer count;
}
