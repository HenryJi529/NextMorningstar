package com.morningstar.proxy.service;

import com.morningstar.proxy.pojo.bo.NodeDetail;
import com.morningstar.proxy.pojo.vo.req.CreateNodeRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateNodeRequestVo;

import java.util.List;

public interface NodeService {
    NodeDetail create(CreateNodeRequestVo vo);

    List<NodeDetail> getAll();

    void delete(Integer id);

    NodeDetail update(UpdateNodeRequestVo vo);

    NodeDetail get(Integer id);
}
