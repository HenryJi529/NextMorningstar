package com.morningstar.kill.pojo.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 用户每日战绩
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@HeadRowHeight(value = 25) // 表头行高
@ContentRowHeight(value = 20) // 内容行高
@ColumnWidth(value = 20) // 列宽
public class UserDailyStats {
    @ExcelProperty(value = {"玩家战绩信息", "日期"}, index = 0)
    @DateTimeFormat("yyyy/MM/dd")
    private LocalDate date;
    @ExcelProperty(value = {"玩家战绩信息", "身份场胜率"}, index = 1)
    private String identityModeRatio;
    @ExcelProperty(value = {"玩家战绩信息", "斗地主胜率"}, index = 2)
    private String revertModeRatio;
    @ExcelProperty(value = {"玩家战绩信息", "国战胜率"}, index = 3)
    private String nationModeRatio;
    @ExcelProperty(value = {"玩家战绩信息", "1V1胜率"}, index = 4)
    private String soloModeRatio;
    @ExcelProperty(value = {"玩家战绩信息", "2V2胜率"}, index = 5)
    private String doublesModeRatio;
    @ExcelProperty(value = {"玩家战绩信息", "3V3胜率"}, index = 6)
    private String triplesModeRatio;

}
