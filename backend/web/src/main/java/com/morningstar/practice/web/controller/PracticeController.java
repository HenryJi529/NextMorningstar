package com.morningstar.practice.web.controller;

import cn.hutool.json.JSONObject;
import com.morningstar.response.R;
import com.morningstar.practice.pojo.bo.GraphData;
import com.morningstar.practice.pojo.po.Person;
import com.morningstar.practice.service.PracticeService;
import com.morningstar.practice.service.SmallWorldNetworkService;
import com.morningstar.validation.constraint.FileNotEmpty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import java.util.*;


@Tag(name = "自由练习相关接口定义")
@RestController
@Slf4j
@RequestMapping("/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;
    private final SmallWorldNetworkService smallWorldNetworkService;

    private final RestTemplate restTemplate;

    private final String imageUrl = "https://picsum.photos/1200/1800";

    @Operation(summary = "通过ResponseEntity获取字符串")
    @GetMapping("/response-entity/string")
    public ResponseEntity<String> getStringByResponseEntity() {
        return ResponseEntity.status(666).body("我们永远赢不麻");
    }

    @Operation(summary = "通过ResponseEntity获取JSON[会自动设置Content-Type]")
    @GetMapping("/response-entity/json1")
    public ResponseEntity<Map<String, Object>> getJsonByResponseEntity1() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "1");
        map.put("key2", 2);
        return ResponseEntity.status(666).body(map);
    }

    @Operation(summary = "通过ResponseEntity获取JSON字符串[不会自动设置Content-Type]")
    @GetMapping("/response-entity/json2")
    public ResponseEntity<String> getJsonByResponseEntity2() {
        JSONObject json = new JSONObject();
        json.set("key1", "1");
        json.set("key2", 2);
        return ResponseEntity.status(666).body(json.toString());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "无响应体[204]")
    @GetMapping("/http-status/204")
    public void httpStatus204() {
    }

    @Operation(summary = "异步任务")
    @GetMapping("/async-task")
    public R<String> asyncTask() {
        practiceService.longTask();
        return R.ok("任务正在运行...");
    }

    @Operation(summary = "使用InputStreamResource下载图片")
    @GetMapping("/download/input-stream-resource")
    public ResponseEntity<InputStreamResource> downloadByInputStreamResource() {
        byte[] imageBytes = Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class));

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(new ByteArrayInputStream(imageBytes)));
    }

    @Operation(summary = "使用byte[]下载图片")
    @GetMapping("/download/byte-array")
    public ResponseEntity<byte[]> downloadByByteArray() {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class)));
    }

    @Operation(summary = "使用HttpServletResponse下载图片")
    @GetMapping("/download/http-servlet-response")
    public void downloadByHttpServletResponse(HttpServletResponse response) {
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

    @Operation(summary = "MultipartFile上传文件和描述信息")
    @PostMapping("/upload-file-by-multipart-file")
    public R<Map<String, Object>> uploadFileByMultipartFile(
            @RequestParam("file") @FileNotEmpty() MultipartFile file,
            @RequestParam("description") String description
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("filename", file.getOriginalFilename());
        response.put("size", file.getSize());
        response.put("description", description);

        return R.ok(response);
    }

    @Operation(summary = "后端解析Excel+测速")
    @PostMapping("/parse-excel")
    public R<Map<String, Object>> parseExcel(@RequestParam("file") @FileNotEmpty() MultipartFile file) {
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

    @Operation(summary = "模拟长时间运行的任务")
    @PostMapping("/long-task")
    public R<Object> longTask() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("程序执行完成...");
        return R.ok("Good Job");
    }

    @Operation(summary = "使用StreamingResponseBody实现流式响应")
    @GetMapping("/streaming-response/streaming-response-body")
    public StreamingResponseBody streamingResponseByStreamingResponseBody() {
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

    @Operation(summary = "使用ResponseBodyEmitter实现流式响应")
    @GetMapping("/streaming-response/response-body-emitter")
    public ResponseBodyEmitter streamingResponseByResponseBodyEmitter() {
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

    @Operation(summary = "使用SseEmitter实现流式响应")
    @GetMapping("/streaming-response/sse-emitter")
    public SseEmitter streamingResponseBySseEmitter() {
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

    @Operation(summary = "根据姓名片段查询演员列表")
    @GetMapping("/small-world-network/find-persons-by-name")
    public R<List<Person>> findPersonsByName(@RequestParam("name") String name){
        return R.ok(smallWorldNetworkService.findByNameContainingIgnoreCase(name));
    }

    @Operation(summary = "查询两个演员之间的最短路径")
    @GetMapping("/small-world-network/find-shortest-path-between-two-persons")
    public R<GraphData> findShortestPathBetweenTwoPersons(@RequestParam("person1Id") Long person1Id, @RequestParam("person2Id") Long person2Id) {
        return R.ok(smallWorldNetworkService.getShortestPath(person1Id, person2Id));
    }

    @Data
    public static class Author {
        // NOTE: OpenAPI 不支持 @Length
        @Size(message = "作者名不能超过10个字符", max = 10, groups = Group.Create.class)
        private String name;
        // NOTE: OpenAPI 不支持 @Positive
        @Min(value = 0, message = "年龄必须大于0", groups = Group.Update.class)
        private Integer age;
    }

    @Data
    public static class Book {
        @NotBlank(message = "书名不能为空", groups = Group.Create.class)
        private String name;
        @Min(message = "销量不能低于0", value = 0, groups = Group.Create.class)
        private Integer sales;
        @Valid
        private Author author;
    }

    public static class Group {
        public interface Create {}
        public interface Update {}
    }

    @Operation(summary = "验证创建参数")
    @PostMapping("/valid-input/create")
    public R<Object> validCreateInput(@RequestBody @Validated(Group.Create.class) Book book) {
        return R.ok(book);
    }

    @Operation(summary = "验证更新参数")
    @PostMapping("/valid-input/update")
    public R<Object> validUpdateInput(@RequestBody @Validated(Group.Update.class) Book book) {
        return R.ok(book);
    }
}

