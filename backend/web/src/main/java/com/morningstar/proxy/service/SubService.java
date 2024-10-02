package com.morningstar.proxy.service;

import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.pojo.vo.req.CreateSubRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateSubRequestVo;

import java.util.List;

public interface SubService {
    Sub create(CreateSubRequestVo vo);

    List<Sub> getAll();

    Sub get(Integer id);

    Sub update(UpdateSubRequestVo vo);

    void delete(Integer id);

    void sync(Sub sub);
}
