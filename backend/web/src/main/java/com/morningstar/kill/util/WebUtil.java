package com.morningstar.kill.util;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.kill.constant.HttpConstant;
import com.morningstar.kill.pojo.vo.resp.R;
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
        try{
            String respStr = new ObjectMapper().writeValueAsString(r);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(200);
            response.getWriter().write(respStr);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    public static void renderExcel(List<?> o, String sheetName, String fileName, Class<?> clazz, HttpServletResponse response) {
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(HttpConstant.CONTENT_TYPE_XLS);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            EasyExcel
                    .write(response.getOutputStream(),clazz)
                    .sheet(sheetName)
                    .doWrite(o);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
