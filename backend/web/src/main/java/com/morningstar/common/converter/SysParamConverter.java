package com.morningstar.common.converter;

import com.morningstar.common.pojo.po.SysParam;
import com.morningstar.common.pojo.vo.req.CreateSysParamRequestVo;
import com.morningstar.common.pojo.vo.req.UpdateSysParamRequestVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SysParamConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysParam createSysParamRequestVo2SysParam(CreateSysParamRequestVo vo);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysParam updateSysParamRequestVo2SysParam(UpdateSysParamRequestVo vo);

}
