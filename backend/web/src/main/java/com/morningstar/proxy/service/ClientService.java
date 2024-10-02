package com.morningstar.proxy.service;

import com.morningstar.proxy.pojo.po.Node;

import java.util.List;

public interface ClientService {
    List<Node> getAssociatedNodes(String token);
}
