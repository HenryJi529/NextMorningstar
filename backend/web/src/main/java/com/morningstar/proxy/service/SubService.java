package com.morningstar.proxy.service;

import com.morningstar.constant.R;
import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.pojo.vo.req.CreateSubRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateSubRequestVo;

import java.util.List;

public interface SubService {
    R<Sub> create(CreateSubRequestVo vo);

    R<List<Sub>> getAll();

    R<Sub> get(Integer id);

    R<Sub> update(UpdateSubRequestVo vo);

    R<Object> delete(Integer id);

    void sync(Sub sub);
}
