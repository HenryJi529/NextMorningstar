package com.morningstar.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.common.converter.SysParamConverter;
import com.morningstar.common.dao.mapper.SysParamMapper;
import com.morningstar.common.pojo.po.SysParam;
import com.morningstar.common.pojo.vo.req.CreateSysParamRequestVo;
import com.morningstar.common.pojo.vo.req.UpdateSysParamRequestVo;
import com.morningstar.common.service.SystemService;
import com.morningstar.response.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final SysParamMapper sysParamMapper;
    private final SysParamConverter sysParamConverter;

    @Override
    public SysParam getSysParamByName(String name) {
        LambdaQueryWrapper<SysParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysParam::getName, name);
        SysParam dBSysParam = sysParamMapper.selectOne(wrapper);
        if(dBSysParam == null){
            throw new BaseException(ResponseCode.SYS_PARAM_NAME_NOT_FOUND, name);
        }else if(dBSysParam.getScope() == SysParam.Scope.INTERNAL && !AuthUtil.getLoginUser().getPermissions().contains("sys:param:manage")){
            throw new BaseException(ResponseCode.SYS_PARAM_ACCESS_DENIED, name);
        } else {
            return dBSysParam;
        }
    }

    @Override
    public List<SysParam> getAllSysParam() {
        return sysParamMapper.selectList(null);
    }

    @Override
    public Long createSysParam(CreateSysParamRequestVo vo) {
        SysParam dbSysParam = sysParamConverter.createSysParamRequestVo2SysParam(vo);
        try {
            sysParamMapper.insert(dbSysParam);
        } catch (DuplicateKeyException e){
            throw new BaseException(ResponseCode.SYS_PARAM_CREATE_FAILED);
        }
        return dbSysParam.getId();
    }

    @Override
    public void updateSysParam(UpdateSysParamRequestVo vo) {
        SysParam dbSysParam = sysParamConverter.updateSysParamRequestVo2SysParam(vo);
        try {
            sysParamMapper.updateById(dbSysParam);
        } catch (DuplicateKeyException e){
            throw new BaseException(ResponseCode.SYS_PARAM_UPDATE_FAILED, vo.getId());
        }
    }

    @Override
    public void deleteSysParam(Long id) {
        int count = sysParamMapper.deleteById(id);
        if(count == 0){
            throw new BaseException(ResponseCode.SYS_PARAM_DELETE_FAILED, id);
        }
    }

    @Override
    public SysParam getSysParamById(Long id) {
        SysParam dbSysParam = sysParamMapper.selectById(id);
        if(dbSysParam == null){
            throw new BaseException(ResponseCode.SYS_PARAM_ID_NOT_FOUND, id);
        }
        return dbSysParam;
    }
}
