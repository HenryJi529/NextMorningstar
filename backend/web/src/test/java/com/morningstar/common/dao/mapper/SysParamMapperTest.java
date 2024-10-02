package com.morningstar.common.dao.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.common.pojo.po.SysParam;
import com.morningstar.util.JsonUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysParamMapperTest {

    private final SysParamMapper sysParamMapper;

    @Test
    public void test() throws JsonProcessingException {
        SysParam sysParam = SysParam.builder().name("testName").value("testValue").scope(SysParam.Scope.PUBLIC).build();
        System.out.println(JsonUtil.objectMapper().writeValueAsString(sysParam));
        sysParamMapper.insert(sysParam);
        System.out.println(JsonUtil.objectMapper().writeValueAsString(sysParam));
        System.out.println(Arrays.toString(sysParamMapper.selectList(null).toArray()));
    }
}
