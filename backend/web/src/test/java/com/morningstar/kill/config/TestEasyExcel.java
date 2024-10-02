package com.morningstar.kill.config;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.morningstar.kill.pojo.bo.UserDailyStats;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {
    private final String path = "temp/test.xlsx";

    private List<UserDailyStats> init(){
        List<UserDailyStats> userDailyStatsList = new ArrayList<>();
        UserDailyStats userDailyStats1 = new UserDailyStats(LocalDate.now(), "3/10", "9/10", "5/8", "6/6", "7/10", "12/20");
        UserDailyStats userDailyStats2 = new UserDailyStats(LocalDate.now().minusDays(1),  "9/12", "8/19", "18/20", "21/40", "8/10","10/32");
        userDailyStatsList.add(userDailyStats1);
        userDailyStatsList.add(userDailyStats2);
        return userDailyStatsList;
    }

    @Test
    public void testWrite() {
        List<UserDailyStats> userDailyStats = init();
        //不做任何注解处理时，表头名称与实体类属性名称一致
        System.out.println(userDailyStats);
        EasyExcel.write(path, UserDailyStats.class).sheet("玩家胜率信息").doWrite(userDailyStats);
    }

    @Test
    public void testRead() {
        List<UserDailyStats> userDailyStats = new ArrayList<>();
        //读取数据
        EasyExcel.read(path, UserDailyStats.class, new AnalysisEventListener<UserDailyStats>() {
            @Override
            public void invoke(UserDailyStats o, AnalysisContext analysisContext) {
                System.out.println(o);
                userDailyStats.add(o);
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("完成。。。。");
            }
        }).sheet().doRead();
        System.out.println(userDailyStats);
    }
}
