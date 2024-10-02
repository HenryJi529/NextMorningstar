package com.morningstar.kill.dao.mapper;

import com.morningstar.util.FakerUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RecordMapperTest {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private FakerUtil fakerUtil;

    @Test
    public void testInsertIntoRecordAndUserRecord() {
        recordMapper.insertIntoRecordAndUserRecord(fakerUtil.fakeKillRecord());
    }
}
