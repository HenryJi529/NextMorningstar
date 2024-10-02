package com.morningstar.proxy.service;

import com.morningstar.constant.R;
import com.morningstar.proxy.pojo.bo.NodeDetail;
import com.morningstar.proxy.pojo.vo.req.CreateNodeRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateNodeRequestVo;

import java.util.List;

public interface NodeService {
    R<NodeDetail> create(CreateNodeRequestVo vo);

    R<List<NodeDetail>> getAll();

    R<Object> delete(Integer id);

    R<NodeDetail> update(UpdateNodeRequestVo vo);

    R<NodeDetail> get(Integer id);
}
