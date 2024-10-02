package com.morningstar.kill.service;

import jakarta.servlet.http.HttpServletResponse;

public interface RecordService {
    void exportDailyStats(int dayNum, HttpServletResponse response);
}
