package com.morningstar.kill.web.controller;

import com.morningstar.constant.R;
import com.morningstar.kill.hero.Hero;
import com.morningstar.kill.hero.HeroPool;
import com.morningstar.kill.pojo.bo.UserSeasonRank;
import com.morningstar.kill.pojo.bo.UserSeasonStats;
import com.morningstar.kill.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "仿三国杀相关接口定义")
@RestController
@RequestMapping("/kill")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class KillController {
    private final RecordService recordService;

    @Operation(summary = "英雄描述获取", description = "根据英雄名获取英雄描述")
    @GetMapping("/hero")
    public R<Hero> getHero(@Valid @NotBlank(message = "英雄名不能为空") @RequestParam(name = "hero") String heroName) {
        return R.ok(HeroPool.getHeroByName(heroName));
    }

    /**
     * 每日战绩导出
     */
    @Parameter(name = "dayNum", description = "天数", in = ParameterIn.QUERY)
    @Operation(summary = "每日战绩导出", description = "导出近{dayNum}天的各游戏模式胜率")
    @GetMapping("/stats/daily")
    public void exportDailyStats(@RequestParam(name = "dayNum", required = false, defaultValue = "10") int dayNum, HttpServletResponse response) {
        recordService.exportDailyStats(dayNum, response);
    }

    /**
     * 赛季表现获取
     */
    @Operation(summary = "赛季战绩获取")
    @GetMapping("/stats/season")
    public R<UserSeasonStats> getSeasonStats() {
        // TODO: 实现功能
        return R.ok(new UserSeasonStats(90.0, 80.0, 70.52122, 60.4567, 90.21, 43.897));
    }

    /**
     * 赛季排名获取
     */
    @Operation(summary = "赛季排名获取")
    @GetMapping("/rank/season")
    public R<UserSeasonRank> getSeasonRank() {
        // TODO: 实现功能
        return R.ok(new UserSeasonRank(1, 43, 123, 5445, 65423, 546122));
    }
}
