package com.morningstar.system.service;

import com.morningstar.system.pojo.po.SysParam;
import com.morningstar.system.pojo.vo.req.CreateSysParamRequestVo;
import com.morningstar.system.pojo.vo.req.UpdateSysParamRequestVo;

import java.util.List;

public interface SystemService {
    SysParam getSysParamByName(String name);

    List<SysParam> getAllSysParam();

    Long createSysParam(CreateSysParamRequestVo vo);

    void updateSysParam(UpdateSysParamRequestVo vo);

    void deleteSysParam(Long id);

    SysParam getSysParamById(Long id);
}
