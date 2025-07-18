package com.morningstar.pic.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.morningstar.exception.BaseException;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ImageService;
import com.morningstar.util.GithubUtil;
import com.morningstar.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageServiceImpl implements ImageService {
    private final GithubUtil githubUtil;
    private final ConfigService configService;

    @Value("${morningstar.app.pic.repo-name}")
    private String repoName;

    private final RestTemplate restTemplate;

    @Override
    public List<Image> getImages() {
        String pat = configService.getGithubPat();
        List<String> dirs = new ArrayList<>();
        List<Image> images = new ArrayList<>();
        for(JsonNode dirNode : githubUtil.getFileOrDir(pat, repoName, "")){
            String path = dirNode.get("path").asText();
            if(path.matches("[0-9]{8}")){
                dirs.add(path);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        for(String dir : dirs){
            for(JsonNode fileNode: githubUtil.getFileOrDir(pat, repoName, dir)){
                String filename = fileNode.get("name").asText();
                String path = fileNode.get("path").asText();
                long size = fileNode.get("size").asLong();
                try{
                    LocalDateTime updateTime = LocalDateTime.parse(path.split("-")[0].replace("/", ""), formatter);
                    images.add(Image.builder().filename(filename).path(path).size(size).updateTime(updateTime).build());
                }catch (DateTimeParseException ignored){
                }
            }
        }
        images.sort((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()));
        return images;
    }

    private String getFilePath(String originFilename){
        LocalDateTime now = LocalDateTime.now();
        String dir = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now).replace("-", "");
        return dir + "/" + now.format(DateTimeFormatter.ofPattern("HHmmssSSS")) + "-" + originFilename;
    }

    @Override
    public String uploadLargeImage(MultipartFile file) {
        String pat = configService.getGithubPat();
        try {
            byte[] content = file.getResource().getContentAsByteArray();
            JsonNode result = githubUtil.createFile(pat, repoName, getFilePath(file.getOriginalFilename()), content);
            return result.get("content").get("path").asText();
        } catch (IOException ignored) {
            throw new BaseException("图片上传失败");
        }
    }

    @Override
    public String uploadSmallImage(UploadSmallImageRequestVo vo) {
        String pat = configService.getGithubPat();
        JsonNode result = githubUtil.createFile(pat, repoName, getFilePath(vo.getFilename()), vo.getContent());
        return result.get("content").get("path").asText();
    }

    @Override
    public void deleteImage(String path) {
        String pat = configService.getGithubPat();
        try{
            githubUtil.deleteFile(pat, repoName, path);
        }catch (HttpClientErrorException.NotFound e){
            throw new BaseException(String.format("文件[%s]不存在", path));
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> relayDownload(String ownerName, String dirName, String fileName) {
        if(!ImageUtil.isImageFilename(fileName)){
            throw new BaseException("目标不是图片");
        }
        String url = String.format("https://raw.githubusercontent.com/%s/%s/main/%s/%s", ownerName, repoName, dirName, fileName);
        ResponseEntity<Resource> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Resource.class
        );
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new BaseException("图片不存在");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(outputStream -> {
                    try {
                        InputStream inputStream = Objects.requireNonNull(response.getBody()).getInputStream();
                        inputStream.transferTo(outputStream); // 流式传输到 HTTP 响应
                    } catch (Exception e) {
                        throw new BaseException("下载流传输失败: " + e.getMessage());
                    }
                });
    }
}
