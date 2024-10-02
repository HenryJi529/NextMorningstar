package com.morningstar.practice.lib;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

public class EasyExcelTest {
    @Test
    public void testWrite() {
        EasyExcel.write("temp/easyexcel_write.xlsx", FillData.class).sheet("基本信息").doWrite(getFillDataList());
    }

    @Test
    public void testRead() {
        List<FillData> fillDataList = new ArrayList<>();

        String path = "/excel/easyexcel_read.xlsx";

        EasyExcel.read(getClass().getResourceAsStream(path), FillData.class, new AnalysisEventListener<FillData>() {
            @Override
            public void invoke(FillData o, AnalysisContext analysisContext) {
                System.out.println(o);
                fillDataList.add(o);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("完成。。。。");
            }
        }).sheet().doRead();
        System.out.println(fillDataList);

        List<FillData> dataList = EasyExcel.read(getClass().getResourceAsStream(path))
                .head(FillData.class)
                .headRowNumber(3)  // 跳过前两行表头（匹配图片中的表头层级）
                .sheet()
                .doReadSync();
        System.out.println(Arrays.toString(dataList.toArray()));
    }

    @Test
    public void testWriteWithTemplate1() throws URISyntaxException {
        File template = new File(Objects.requireNonNull(this.getClass().getResource("/excel/easyexcel_template1.xlsx")).toURI());
        EasyExcel.write("temp/easyexcel_writeWithTemplate1.xlsx").withTemplate(template).sheet().doFill(
                FillData.builder().name("张三").number(5.2).build()
        );
    }

    @Test
    public void testWriteWithTemplate2() throws URISyntaxException {
        File template = new File(Objects.requireNonNull(this.getClass().getResource("/excel/easyexcel_template2.xlsx")).toURI());
        // 这里 会填充到第一个sheet， 然后文件流会自动关闭
        EasyExcel.write("temp/easyexcel_writeWithTemplate2_1.xlsx").withTemplate(template).sheet().doFill(getFillDataList());

        // 分多次 填充 会使用文件缓存（省内存）
        try (ExcelWriter excelWriter = EasyExcel.write("temp/easyexcel_writeWithTemplate2_2.xlsx").withTemplate(template).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(getFillDataList(), writeSheet);
            excelWriter.fill(getFillDataList(), writeSheet);
        }
    }

    @Test
    public void testWriteWithTemplate3() {
        try (ExcelWriter excelWriter = EasyExcel.write("temp/easyexcel_writeWithTemplate3.xlsx").withTemplate(getClass().getResourceAsStream("/excel/easyexcel_template3.xlsx")).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            // 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
            // forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(getFillDataList(), fillConfig, writeSheet);
            excelWriter.fill(getFillDataList(), fillConfig, writeSheet);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("date", "2019年10月9日13:28:28");
            map.put("total", 1000);
            excelWriter.fill(map, writeSheet);
        }
    }

    private List<FillData> getFillDataList() {
        List<FillData> fillDataList = new ArrayList<>();
        fillDataList.add(FillData.builder().name("小白").number(31.1).date(new Date(1630000000000L)).build());
        fillDataList.add(FillData.builder().name("小黑").number(32.2).date(new Date()).build());

        return fillDataList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @HeadRowHeight(value = 25) // 表头行高
    @ContentRowHeight(value = 20) // 内容行高
    @ColumnWidth(value = 20) // 列宽
    public static class FillData {
        @ExcelProperty({"基本信息", "名称"})
        private String name;
        @ExcelProperty({"基本信息", "数字"})
        private double number;
        @ExcelProperty(value = {"日期"}, index = 2)
        private Date date;
    }
}
