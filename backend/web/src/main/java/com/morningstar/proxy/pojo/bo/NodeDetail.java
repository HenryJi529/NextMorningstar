package com.morningstar.proxy.pojo.bo;

import com.morningstar.proxy.pojo.po.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeDetail extends Node {
    private String name;
    private String protocol;
}