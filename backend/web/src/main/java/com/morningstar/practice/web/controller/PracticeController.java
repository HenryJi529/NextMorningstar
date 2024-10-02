package com.morningstar.practice.web.controller;

import cn.hutool.json.JSONObject;
import com.morningstar.constant.R;
import com.morningstar.practice.pojo.AData;
import com.morningstar.practice.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Tag(name = "自由练习相关接口定义")
@RestController
@RequestMapping("/practice")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PracticeController {

    private final PracticeService practiceService;

    private final RestTemplate restTemplate;

    private final String imageUrl = "https://picsum.photos/1200/1800";

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

    @Operation(summary = "接口C")
    @GetMapping("/c")
    public R<String> c(){
        practiceService.prolongedTask();
        return R.ok();
    }

    @Operation(summary = "接口D1")
    @GetMapping("/d1")
    public ResponseEntity<InputStreamResource> d1() {
        byte[] imageBytes = Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class));

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(new ByteArrayInputStream(imageBytes)));
    }

    @Operation(summary = "接口D2")
    @GetMapping("/d2")
    public ResponseEntity<byte[]> d2() {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(Objects.requireNonNull(restTemplate.getForObject(imageUrl, byte[].class)));
    }

    @Operation(summary = "接口D3")
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

    @Operation(summary = "接口E")
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
}

