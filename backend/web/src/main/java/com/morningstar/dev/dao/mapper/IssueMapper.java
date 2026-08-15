package com.morningstar.dev.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.dev.pojo.po.Issue;

public interface IssueMapper extends BaseMapper<Issue> {

    Double calcSavedPersonDays();
}
