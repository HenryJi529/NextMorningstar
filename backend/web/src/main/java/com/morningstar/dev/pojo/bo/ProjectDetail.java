package com.morningstar.dev.pojo.bo;

import com.morningstar.dev.pojo.po.Project;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProjectDetail extends Project {
    private String adminName;
}
