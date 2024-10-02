package com.morningstar.practice.lib;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PoiTest {
    @Test
    public void testWrite() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("nice");
            XSSFRow headRow = sheet.createRow(0);
            headRow.createCell(0).setCellValue("姓名");
            headRow.createCell(1).setCellValue("年龄");

            String[] names = {"小吉", "小邵", "老李"};
            String[] ages = {"27", "25", "46"};
            for (int i = 0; i < names.length; i++) {
                XSSFRow bodyRow = sheet.createRow(i + 1);
                bodyRow.createCell(0).setCellValue(names[i]);
                bodyRow.createCell(1).setCellValue(ages[i]);
            }
            workbook.write(new FileOutputStream("temp/poi_write.xlsx"));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testWriteWithTemplate() {
        try (InputStream template = PoiTest.class.getClassLoader().getResourceAsStream("excel/poi_template.xlsx")) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(Objects.requireNonNull(template))) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row = sheet.createRow(1);
                row.createCell(0).setCellValue("小梁");
                row.createCell(1).setCellValue(24);

                workbook.write(new FileOutputStream("temp/poi_writeWithTemplate.xlsx"));
            }
        } catch (IOException ignored) {
        }
    }

    @Test
    public void testRead() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(Objects.requireNonNull(getClass().getResourceAsStream("/excel/poi_read.xlsx")))) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            System.out.println("首行: " + sheet.getFirstRowNum());
            System.out.println("尾行: " + sheet.getLastRowNum());
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    sb.append(row.getCell(j).getStringCellValue());
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                System.out.println(sb);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
