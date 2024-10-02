package com.morningstar.kill.dao.mapper;

import com.morningstar.kill.pojo.po.Record;
import com.morningstar.kill.util.FakerUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TestRecordMapper {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private FakerUtil fakerUtil;

    @Test
    public void testInsertRecordAndUserRecord() {
        recordMapper.insertRecordAndUserRecord(fakerUtil.fakeRecord());
    }

    @Test
    public void testSelectAll() {
        List<Record> recordList = recordMapper.selectAll();
        System.out.println(recordList);
    }
}
