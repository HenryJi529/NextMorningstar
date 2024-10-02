package com.morningstar.practice.lib;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EasyPoiTest {
    @Test
    public void testWrite() throws IOException {
        List<User> users = new ArrayList<>();
        users.add(new User("张三", 25, "1"));
        users.add(new User("李四", 30, "2"));

        // 导出参数设置
        ExportParams exportParams = new ExportParams("用户信息", "用户表");

        // 生成Workbook
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, User.class, users);
        System.out.println(workbook);

        // 输出到文件
        FileOutputStream fos = new FileOutputStream("temp/easypoi_write.xlsx");
        workbook.write(fos);
        fos.close();
    }

    @Data
    @AllArgsConstructor
    public static class User {
        @Excel(name = "姓名")
        private String name;

        @Excel(name = "年龄", orderNum = "1")
        private Integer age;

        @Excel(name = "性别", replace = {"男_1", "女_2"}, orderNum = "2")
        private String gender;
    }
}
