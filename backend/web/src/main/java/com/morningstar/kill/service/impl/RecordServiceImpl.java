package com.morningstar.kill.service.impl;

import com.morningstar.common.pojo.bo.LoginUser;
import com.morningstar.kill.dao.mapper.RecordMapper;
import com.morningstar.kill.pojo.bo.UserDailyStats;
import com.morningstar.kill.service.RecordService;
import com.morningstar.util.WebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RecordServiceImpl implements RecordService {
    private final RecordMapper recordMapper;

    @Override
    public void exportDailyStats(int dayNum, HttpServletResponse response) {
        // 获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        UUID userId = loginUser.getUser().getId();
        String username = loginUser.getUsername();

        // 读取当前用户的每日战绩
        List<UserDailyStats> userDailyStatsList = recordMapper.selectRecentDailyStatsByUserId(dayNum, userId);

        // 输出为Excel
        String fileName = String.format("近%d日%s战绩信息.xlsx", dayNum, username);
        String sheetName = String.format("%s战绩信息(近%d日)", username, dayNum);
        WebUtil.renderExcel(userDailyStatsList, sheetName, fileName, UserDailyStats.class, response);
    }
}
