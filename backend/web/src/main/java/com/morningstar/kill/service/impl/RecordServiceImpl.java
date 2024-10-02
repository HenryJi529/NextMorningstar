package com.morningstar.kill.service.impl;

import com.morningstar.common.pojo.bo.LoginUser;
import com.morningstar.exception.BaseException;
import com.morningstar.kill.dao.mapper.RecordMapper;
import com.morningstar.kill.pojo.bo.UserDailyModeStats;
import com.morningstar.kill.pojo.bo.UserDailyStats;
import com.morningstar.kill.service.RecordService;
import com.morningstar.util.TimeUtil;
import com.morningstar.util.WebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
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

        // 获取当前用户每日各模式的胜率数据，并整理为每日总体数据
        List<UserDailyModeStats> userDailyModeStatsList = recordMapper.selectDailyModeStats(
                userId,
                TimeUtil.getCurrentLocalDate().minusDays(dayNum - 1),
                TimeUtil.getCurrentLocalDate()
        );
        List<UserDailyStats> userDailyStatsList = new ArrayList<>();
        LocalDate today = TimeUtil.getCurrentLocalDate();
        String defaultRatio = "0/0";
        for (int i = 0; i < dayNum; i++) {
            LocalDate date = today.minusDays(i);
            UserDailyStats userDailyStats = UserDailyStats
                    .builder()
                    .date(date)
                    .doublesModeRatio(defaultRatio)
                    .identityModeRatio(defaultRatio)
                    .nationModeRatio(defaultRatio)
                    .revertModeRatio(defaultRatio)
                    .soloModeRatio(defaultRatio)
                    .triplesModeRatio(defaultRatio)
                    .build();
            userDailyStatsList.add(userDailyStats);
        }
        userDailyModeStatsList.forEach(userDailyModeStats -> {
            UserDailyStats userDailyStats = userDailyStatsList
                    .stream()
                    .filter((item) -> item.getDate().equals(userDailyModeStats.getDate()))
                    .toList().get(0);

            try {
                Method method = userDailyStats.getClass().getMethod("set" + StringUtils.capitalize(userDailyModeStats.getMode().toLowerCase()) + "ModeRatio", String.class);
                method.invoke(userDailyStats, userDailyModeStats.getRatio());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new BaseException(e.getMessage());
            }
        });

        // 输出为Excel
        String fileName = String.format("近%d日%s战绩信息.xlsx", dayNum, username);
        String sheetName = String.format("%s战绩信息(近%d日)", username, dayNum);
        WebUtil.renderSimpleExcel(userDailyStatsList, sheetName, fileName, UserDailyStats.class, response);
    }
}
