package com.morningstar.common.service;

import com.morningstar.common.pojo.po.SysParam;
import com.morningstar.common.pojo.vo.req.CreateSysParamRequestVo;
import com.morningstar.common.pojo.vo.req.UpdateSysParamRequestVo;

import java.util.List;

public interface SystemService {
    SysParam getSysParamByName(String name);

    List<SysParam> getAllSysParam();

    Long createSysParam(CreateSysParamRequestVo vo);

    void updateSysParam(UpdateSysParamRequestVo vo);

    void deleteSysParam(Long id);

    SysParam getSysParamById(Long id);
}
