package com.morningstar.pic.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.morningstar.constant.RedisConstant;
import com.morningstar.exception.*;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ResourceService;
import com.morningstar.util.GithubUtil;
import com.morningstar.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceServiceImpl implements ResourceService {
    private final GithubUtil githubUtil;
    private final ConfigService configService;
    private final ApplicationContext context;

    @Value("${morningstar.app.pic.repo-name}")
    private String repoName;

    private final RestTemplate restTemplate;

    @Cacheable(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#userId")
    public List<String> getDirList(UUID userId) {
        log.info("从Github查询{}的目录列表", repoName);
        String pat = configService.getGithubPat(userId);
        List<String> dirs = new ArrayList<>();
        for(JsonNode dirNode : githubUtil.getFileOrDir(pat, repoName, "")){
            String path = dirNode.get("path").asText();
            if(path.matches("[0-9]{8}")){
                dirs.add(path);
            }
        }
        return dirs;
    }

    @Cacheable(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#userId")
    public List<Image> getImageList(UUID userId) {
        log.info("从Github查询{}的图片列表", repoName);
        String pat = configService.getGithubPat(userId);
        List<String> dirs = context.getBean(ResourceServiceImpl.class).getDirList(userId);
        List<Image> images = new ArrayList<>();

        for(String dir : dirs){
            for(JsonNode fileNode: githubUtil.getFileOrDir(pat, repoName, dir)){
                String filename = fileNode.get("name").asText();
                if(!ImageUtil.isValidImageFilename(filename)){
                    continue;
                }
                Image image = parseFileNodeToImage(fileNode);
                if (image != null){
                    images.add(image);
                }
            }
        }
        images.sort((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()));
        return images;
    }

    private Image parseFileNodeToImage(JsonNode fileNode){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        String filename = fileNode.get("name").asText();
        String path = fileNode.get("path").asText();
        long size = fileNode.get("size").asLong();
        try{
            LocalDateTime updateTime = LocalDateTime.parse(path.split("-")[0].replace("/", ""), formatter);
            return Image.builder().filename(filename).path(path).size(size).updateTime(updateTime).build();
        }catch (DateTimeParseException ignored){
            return null;
        }
    }

    private List<LocalDate> getUsageDateList(UUID userId){
        return context.getBean(ResourceServiceImpl.class).getDirList(userId).stream().map(dir -> LocalDate.parse(dir, DateTimeFormatter.ofPattern("yyyyMMdd"))).toList();
    }

    @Override
    public boolean isUsageExceeded(UUID userId){
        int quota = 1000;
        List<LocalDate> usageDateList = getUsageDateList(userId);
        if(usageDateList.size() > quota){
            return true;
        } else if (usageDateList.size() == quota) {
            LocalDate latestDate = usageDateList.stream().max(LocalDate::compareTo).orElse(null);
            return Objects.requireNonNull(latestDate).isBefore(LocalDate.now());
        }else{
            return false;
        }
    }

    @Override
    public Usage getUsage(Config config) {
        List<Image> images = context.getBean(ResourceServiceImpl.class).getImageList(config.getUserId());
        List<LocalDate> dates = getUsageDateList(config.getUserId());

        return Usage.builder()
                .usedStorage(images.stream().mapToLong(Image::getSize).sum())
                .imageCount(images.size())
                .dayCount(dates.size())
                .build();
    }

    private String getFilePath(String originFilename){
        LocalDateTime now = LocalDateTime.now();
        String dir = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now).replace("-", "");
        return dir + "/" + now.format(DateTimeFormatter.ofPattern("HHmmssSSS")) + "-" + originFilename;
    }

    @Override
    public List<Image> getImages(Config config) {
        return context.getBean(ResourceServiceImpl.class).getImageList(config.getUserId());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "T(com.morningstar.util.AuthUtil).getUserId()"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "T(com.morningstar.util.AuthUtil).getUserId()")
    })
    public Image uploadLargeImage(MultipartFile file, Config config) {
        String pat = config.getGithubPat();
        try {
            byte[] content = file.getResource().getContentAsByteArray();
            JsonNode result = githubUtil.createFile(pat, repoName, getFilePath(file.getOriginalFilename()), content);
            return parseFileNodeToImage(result.get("content"));
        } catch (IOException ignored) {
            throw new PicImageUploadingException(file.getOriginalFilename());
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "T(com.morningstar.util.AuthUtil).getUserId()"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "T(com.morningstar.util.AuthUtil).getUserId()")
    })
    public Image uploadSmallImage(UploadSmallImageRequestVo vo, Config config) {
        String pat = config.getGithubPat();
        BufferedImage image = ImageUtil.getBufferedImageFromBase64String(vo.getContent());
        System.out.println(image);

        JsonNode result = githubUtil.createFile(pat, repoName, getFilePath(vo.getFilename()), ImageUtil.getPureBase64StringFromDataBase64String(vo.getContent()));
        return parseFileNodeToImage(result.get("content"));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#config.userId"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#config.userId")
    })
    public Image uploadForMWeb(MultipartFile file, Config config) {
        String pat = config.getGithubPat();
        BufferedImage image = ImageUtil.read(file);
        System.out.println(image);
        try {
            byte[] content = file.getResource().getContentAsByteArray();
            JsonNode result = githubUtil.createFile(pat, repoName, getFilePath(file.getOriginalFilename()), content);
            return parseFileNodeToImage(result.get("content"));
        } catch (IOException ignored) {
            throw new PicImageUploadingException(file.getOriginalFilename());
        }
    }

    @Override
    @Caching(evict = @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "T(com.morningstar.util.AuthUtil).getUserId()"))
    public void deleteImage(String path, Config config) {
        String pat = configService.getGithubPat(config.getUserId());
        if(pat == null){
            throw new PicGithubPatUnsetException();
        }
        try{
            githubUtil.deleteFile(pat, repoName, path);
        }catch (HttpClientErrorException.NotFound e){
            throw new BaseException(String.format("文件[%s]不存在", path));
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> relayDownload(String ownerName, String dirName, String fileName) {
        if(!ImageUtil.isValidImageFilename(fileName)){
            throw new PicImageInvalidFilenameException(fileName);
        }
        String url = String.format("https://raw.githubusercontent.com/%s/%s/main/%s/%s", ownerName, repoName, dirName, fileName);
        ResponseEntity<Resource> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Resource.class
        );
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new PicImageNotFoundException(dirName + "/" + fileName);
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
