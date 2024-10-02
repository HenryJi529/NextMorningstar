package com.morningstar.proxy.pojo.bo;

import com.morningstar.proxy.pojo.po.Node;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeDetail extends Node {
    private String name;
    private String protocol;
}