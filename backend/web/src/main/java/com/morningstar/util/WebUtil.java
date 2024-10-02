package com.morningstar.util;

import com.alibaba.excel.EasyExcel;
import com.morningstar.constant.HttpConstant;
import com.morningstar.constant.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class WebUtil {
    public static void renderJson(R<Object> r, HttpServletResponse response) {
        try {
            String respStr = JsonUtil.objectMapper().writeValueAsString(r);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(200);
            response.getWriter().write(respStr);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void renderSimpleExcel(List<?> o, String sheetName, String fileName, Class<?> clazz, HttpServletResponse response, boolean isXls) {
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setCharacterEncoding("UTF-8");
            if (isXls) {
                response.setContentType(HttpConstant.CONTENT_TYPE_XLS);
            } else {
                response.setContentType(HttpConstant.CONTENT_TYPE_XLSX);
            }
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            EasyExcel
                    .write(response.getOutputStream(), clazz)
                    .sheet(sheetName)
                    .doWrite(o);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void renderSimpleExcel(List<?> o, String sheetName, String fileName, Class<?> clazz, HttpServletResponse response) {
        renderSimpleExcel(o, sheetName, fileName, clazz, response, false);
    }
}
