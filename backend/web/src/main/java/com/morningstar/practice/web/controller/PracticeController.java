package com.morningstar.practice.web.controller;

import cn.hutool.json.JSONObject;
import com.morningstar.constant.R;
import com.morningstar.practice.pojo.bo.AData;
import com.morningstar.practice.pojo.bo.GraphData;
import com.morningstar.practice.pojo.po.Person;
import com.morningstar.practice.service.PracticeService;
import com.morningstar.practice.service.SmallWorldNetworkService;
import com.morningstar.util.TimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Tag(name = "自由练习相关接口定义")
@RestController
@Slf4j
@RequestMapping("/practice")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PracticeController {

    private final PracticeService practiceService;
    private final SmallWorldNetworkService smallWorldNetworkService;

    private final RestTemplate restTemplate;

    private final String imageUrl = "https://picsum.photos/1200/1800";

    @Operation(summary = "接口A: 通过起始时间获取随机数据")
    @GetMapping("/a")
    public R<AData> a(@Valid @RequestParam("begin") @Past(message = "必须是过去的日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate) {
        LocalDate endDate = TimeUtil.getCurrentLocalDate();
        int days = (int) ChronoUnit.DAYS.between(beginDate, endDate);
        Random random = new Random();

        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <= days; i++) {
            dates.add(beginDate.plusDays(i));
        }

        List<Double> values = Arrays.stream(new double[days + 1])
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

    @Operation(summary = "接口B1: 通过ResponseEntity获取字符串")
    @GetMapping("/b/1")
    public ResponseEntity<String> b1() {
        return ResponseEntity.status(666).body("我们永远赢不麻");
    }

    @Operation(summary = "接口B2: 通过ResponseEntity获取JSON")
    @GetMapping("/b/2")
    public ResponseEntity<Map<String, Object>> b2() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "1");
        map.put("key2", 2);
        return ResponseEntity.status(666).body(map);
    }

    @Operation(summary = "接口B3: 通过ResponseEntity获取JSON字符串[会自动设置Content-Type]")
    @GetMapping("/b/3")
    public ResponseEntity<String> b3() {
        JSONObject json = new JSONObject();
        json.set("key1", "1");
        json.set("key2", 2);
        return ResponseEntity.status(666).body(json.toString());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "接口B4: 无响应体[204]")
    @GetMapping("/b/4")
    public void b4() {
    }

    @Operation(summary = "接口C: 异步任务")
    @GetMapping("/c")
    public R<String> c() {
        practiceService.prolongedTask();
        return R.ok("任务正在运行...");
    }

    @Operation(summary = "接口D1: 使用InputStreamResource下载图片")
    @GetMapping("/d1")
    public ResponseEntity<InputStreamResource> d1() {
        byte[] imageBytes = Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class));

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(new ByteArrayInputStream(imageBytes)));
    }

    @Operation(summary = "接口D2: 使用byte[]下载图片")
    @GetMapping("/d2")
    public ResponseEntity<byte[]> d2() {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class)));
    }

    @Operation(summary = "接口D3: 使用HttpServletResponse下载图片")
    @GetMapping("/d3")
    public void d3(HttpServletResponse response) {
        byte[] imageBytes = Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class));
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setContentLength(imageBytes.length);

        try (OutputStream out = response.getOutputStream()) {
            out.write(imageBytes);
            out.flush();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to download image",
                    e
            );
        }
    }

    @Operation(summary = "接口E: MultipartFile上传文件和描述信息")
    @PostMapping("/e")
    public R<Map<String, Object>> e(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description
    ) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件不能为空");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("filename", file.getOriginalFilename());
        response.put("size", file.getSize());
        response.put("description", description);

        return R.ok(response);
    }

    @Operation(summary = "接口F: 后端解析Excel测速")
    @PostMapping("/f")
    public R<Map<String, Object>> f(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件不能为空");
        }
        long startTime = System.currentTimeMillis();
        List<List<String>> fullData = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            sheet.forEach(eachRow -> {
                List<String> rowData = new ArrayList<>();
                eachRow.forEach(eachCell -> {
                    String value;
                    if (eachCell == null) {
                        value = "";
                    } else {
                        value = switch (eachCell.getCellType()) {
                            case STRING -> eachCell.getStringCellValue();
                            case NUMERIC -> String.valueOf(eachCell.getNumericCellValue());
                            case BOOLEAN -> String.valueOf(eachCell.getBooleanCellValue());
                            case FORMULA -> eachCell.getCellFormula();
                            case BLANK -> "";
                            default -> ""; // 其他类型
                        };
                    }
                    rowData.add(value);
                });
                fullData.add(rowData);
            });
        } catch (IOException ignored) {
        }
        long endTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        result.put("duration", endTime - startTime);
        result.put("data", fullData);
        return R.ok(result);
    }

    @Operation(summary = "接口G: 模拟长时间运行的任务")
    @PostMapping("/g")
    public R<Object> g() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("程序执行完成...");
        return R.ok("Good Job");
    }

    @Operation(summary = "接口H1: 使用StreamingResponseBody实现流式响应")
    @GetMapping("/h1")
    public StreamingResponseBody h1() {
        return outputStream -> {
            for (int i = 0; i < 10; i++) {
                outputStream.write(("chunk-" + i + "\n").getBytes());
                outputStream.flush();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Operation(summary = "接口H2: 使用ResponseBodyEmitter实现流式响应")
    @GetMapping("/h2")
    public ResponseBodyEmitter h2() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(); // 默认超时时间30秒

        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send("Log line " + i + "\n");
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @Operation(summary = "接口H3: 使用SseEmitter实现流式响应")
    @GetMapping("/h3")
    public SseEmitter h3() {
        SseEmitter emitter = new SseEmitter(60000L);

        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send(SseEmitter.event()
                            .data("Data " + i));
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        emitter.onCompletion(() -> System.out.println("完成"));
        emitter.onTimeout(() -> System.out.println("超时"));
        emitter.onError((err) -> System.out.println("异常：" + err.getMessage()));

        return emitter;
    }

    @Operation(summary = "接口I1: 根据姓名片段查询演员列表")
    @GetMapping("/i1")
    public R<List<Person>> i1(@RequestParam("name") String name){
        return R.ok(smallWorldNetworkService.findByNameContainingIgnoreCase(name));
    }

    @Operation(summary = "接口I2: 查询两个演员之间的最短路径")
    @GetMapping("/i2")
    public R<GraphData> i2(@RequestParam("person1Id") Long person1Id, @RequestParam("person2Id") Long person2Id) {
        return R.ok(smallWorldNetworkService.getShortestPath(person1Id, person2Id));
    }

    @Operation(summary = "接口J: 测试自动重启")
    @GetMapping("/j")
    public R<String> j() {
        return R.ok("Hello World!!!");
    }
}

