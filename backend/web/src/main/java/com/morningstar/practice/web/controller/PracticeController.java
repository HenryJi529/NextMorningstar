package com.morningstar.practice.web.controller;

import cn.hutool.json.JSONObject;
import com.morningstar.constant.R;
import com.morningstar.practice.pojo.AData;
import com.morningstar.practice.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Tag(name = "自由练习相关接口定义")
@RestController
@RequestMapping("/practice")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PracticeController {

    private final PracticeService practiceService;

    @Operation(summary = "接口A")
    @GetMapping("/a")
    public R<AData> a(@Valid @RequestParam("begin") @Past(message = "必须是过去的日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate){
        LocalDate endDate = LocalDate.now();
        int days = (int) ChronoUnit.DAYS.between(beginDate, endDate);
        Random random = new Random();

        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <= days; i++) {
            dates.add(beginDate.plusDays(i));
        }

        List<Double> values = Arrays.stream(new double[days+1])
                .map(d -> random.nextDouble())
                .boxed()
                .toList();

        return R.ok(
                AData
                .builder()
                        .dates(dates)
                        .values(values)
                .build()
        );
    }

    @Operation(summary = "接口B1")
    @GetMapping("/b/1")
    public ResponseEntity<String> b1(){
        return ResponseEntity.status(666).body("我们永远赢不麻");
    }

    @Operation(summary = "接口B2")
    @GetMapping("/b/2")
    public ResponseEntity<Map<String, Object>> b2(){
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "1");
        map.put("key2", 2);
        return ResponseEntity.status(666).body(map);
    }

    @Operation(summary = "接口B3")
    @GetMapping("/b/3")
    public ResponseEntity<String> b3(){
        JSONObject json = new JSONObject();
        json.set("key1", "1");
        json.set("key2", 2);
        return ResponseEntity.status(666).body(json.toString());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "接口B4")
    @GetMapping("/b/4")
    public void b4() {
    }

    @GetMapping("/c")
    public R<String> c(){
        practiceService.prolongedTask();
        return R.ok();
    }
}

