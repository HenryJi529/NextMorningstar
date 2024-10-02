package com.morningstar.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;

public class RequestUtil {
    public static String getRequestDetails(HttpServletRequest request) {
        StringBuilder details = new StringBuilder();

        // 请求方法
        details.append("Method: ").append(request.getMethod()).append("\n");

        // 请求URL
        details.append("Request URL: ").append(request.getRequestURL()).append("\n");

        // 请求URI
        details.append("Request URI: ").append(request.getRequestURI()).append("\n");

        // 查询字符串
        details.append("Query String: ").append(request.getQueryString()).append("\n");

        // 协议
        details.append("Protocol: ").append(request.getProtocol()).append("\n");

        // 请求头
        details.append("Headers:\n");
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            details.append("  ").append(headerName).append(": ").append(headerValue).append("\n");
        }

        // 请求体
        // 如果需要获取请求体内容，可以根据请求的类型处理
        // 例如，如果是 POST 请求，可以通过 InputStream 获取内容

        return details.toString();
    }
}
