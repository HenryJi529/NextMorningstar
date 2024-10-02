package com.morningstar.proxy.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.proxy.pojo.po.Sub;

import java.util.List;

public interface SubMapper extends BaseMapper<Sub> {
    List<Sub> selectRandomN(int N);
}
