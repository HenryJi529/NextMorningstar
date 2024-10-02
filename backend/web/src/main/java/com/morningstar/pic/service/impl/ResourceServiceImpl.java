package com.morningstar.pic.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.luciad.imageio.webp.CompressionType;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.RedisConstant;
import com.morningstar.exception.*;
import com.morningstar.pic.lib.ImageLinkGenerator;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.ImageDetail;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ResourceService;
import com.morningstar.util.GithubUtil;
import com.morningstar.util.ImageUtil;
import com.morningstar.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final RestTemplate restTemplate;

    @Cacheable(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#userId")
    public List<String> getDirList(UUID userId) {
        log.info("从Github查询{}的目录列表", ImageLinkGenerator.repoName);
        String pat = configService.getGithubPat(userId);
        List<String> dirs = new ArrayList<>();
        for (JsonNode dirNode : githubUtil.getFileOrDir(pat, ImageLinkGenerator.repoName, "")) {
            String path = dirNode.get("path").asText();
            if (path.matches("[0-9]{8}")) {
                dirs.add(path);
            }
        }
        return dirs;
    }

    @Cacheable(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#userId + '-' + #date.format(T(java.time.format.DateTimeFormatter).ofPattern('yyyyMMdd'))")
    public List<Image> getDailyImageList(UUID userId, LocalDate date) {
        log.info("从Github查询{}在{}的图片列表", ImageLinkGenerator.repoName, date);
        String pat = configService.getGithubPat(userId);
        String dir = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<Image> images = new ArrayList<>();
        for (JsonNode fileNode : githubUtil.getFileOrDir(pat, ImageLinkGenerator.repoName, dir)) {
            String filename = fileNode.get("name").asText();
            if (!ImageUtil.isValidImageFilename(filename)) {
                continue;
            }
            Image image = parseFileNodeToImage(fileNode);
            if (image != null) {
                images.add(image);
            }
        }
        images.sort((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()));
        return images;
    }

    public List<Image> getAllImageList(UUID userId) {
        log.info("从Github查询{}的全部图片列表", ImageLinkGenerator.repoName);
        List<String> dirs = context.getBean(ResourceServiceImpl.class).getDirList(userId);
        List<Image> allImages = new ArrayList<>();

        for (String dir : dirs) {
            List<Image> dailyImages = context.getBean(ResourceServiceImpl.class).getDailyImageList(userId, LocalDate.parse(dir, DateTimeFormatter.ofPattern("yyyyMMdd")));
            allImages.addAll(dailyImages);
        }
        allImages.sort((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()));
        return allImages;
    }

    private Image parseFileNodeToImage(JsonNode fileNode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        String filename = fileNode.get("name").asText();
        String path = fileNode.get("path").asText();
        long size = fileNode.get("size").asLong();
        try {
            LocalDateTime updateTime = LocalDateTime.parse(path.split("-")[0].replace("/", ""), formatter);
            return Image.builder().filename(filename).path(path).size(size).updateTime(updateTime).build();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private List<LocalDate> getUsageDateList(UUID userId) {
        return context.getBean(ResourceServiceImpl.class).getDirList(userId).stream().map(dir -> LocalDate.parse(dir, DateTimeFormatter.ofPattern("yyyyMMdd"))).toList();
    }

    @Override
    public boolean isUsageExceeded(UUID userId) {
        int quota = 1000;
        List<LocalDate> usageDateList = getUsageDateList(userId);
        if (usageDateList.size() > quota) {
            return true;
        } else if (usageDateList.size() == quota) {
            LocalDate latestDate = usageDateList.stream().max(LocalDate::compareTo).orElse(null);
            return Objects.requireNonNull(latestDate).isBefore(TimeUtil.getCurrentLocalDate());
        } else {
            return false;
        }
    }

    @Override
    public Usage getUsage(Config config) {
        List<Image> images = getAllImageList(config.getUserId());
        List<LocalDate> dates = getUsageDateList(config.getUserId());

        return Usage.builder()
                .usedStorage(images.stream().mapToLong(Image::getSize).sum())
                .imageCount(images.size())
                .dayCount(dates.size())
                .build();
    }

    private String getFilePath(String originFilename) {
        LocalDateTime now = TimeUtil.getCurrentLocalDateTime();
        String dir = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now).replace("-", "");
        return dir + "/" + now.format(DateTimeFormatter.ofPattern("HHmmssSSS")) + "-" + originFilename;
    }

    @Override
    public PageResult<Image> getImages(Config config, int pageNum, int pageSize, LocalDate startLocalDate, LocalDate endLocalDate) {
        List<Image> allImages = getAllImageList(config.getUserId());
        LocalDateTime startLocalDateTime = LocalDateTime.of(startLocalDate.minusDays(1), LocalTime.MAX);
        LocalDateTime endLocalDateTime = endLocalDate.plusDays(1).atStartOfDay();
        List<Image> rangedImages = allImages.stream()
                .filter(image -> image.getUpdateTime().isAfter(startLocalDateTime) && image.getUpdateTime().isBefore(endLocalDateTime))
                .toList();
        List<Image> limitedImages = rangedImages.stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .toList();
        return new PageResult<>(limitedImages, pageNum, pageSize, rangedImages.size());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#config.getUserId() + '-' + T(com.morningstar.util.TimeUtil).getCurrentLocalDate().format(T(java.time.format.DateTimeFormatter).ofPattern('yyyyMMdd'))", condition = "#result != null"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#config.getUserId()", condition = "#result != null")
    })
    public Image uploadLargeImage(MultipartFile file, Config config) {
        String pat = config.getGithubPat();
        try {
            byte[] content = file.getResource().getContentAsByteArray();
            JsonNode result = githubUtil.createFile(pat, ImageLinkGenerator.repoName, getFilePath(file.getOriginalFilename()), content);
            Image image = parseFileNodeToImage(result.get("content"));
            if (image == null) {
                throw new PicImageUploadingException(file.getOriginalFilename());
            }
            return image;
        } catch (IOException ignored) {
            throw new PicImageUploadingException(file.getOriginalFilename());
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#config.getUserId() + '-' + T(com.morningstar.util.TimeUtil).getCurrentLocalDate().format(T(java.time.format.DateTimeFormatter).ofPattern('yyyyMMdd'))", condition = "#result != null"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#config.getUserId()", condition = "#result != null")
    })
    public Image uploadSmallImage(UploadSmallImageRequestVo vo, Config config) {
        String pat = config.getGithubPat();
        JsonNode result = githubUtil.createFile(pat, ImageLinkGenerator.repoName, getFilePath(vo.getFilename()), ImageUtil.getPureBase64StringFromDataBase64String(vo.getContent()));
        Image image = parseFileNodeToImage(result.get("content"));
        if (image == null) {
            throw new PicImageUploadingException(vo.getFilename());
        }
        return image;
    }

    private ImageDetail convertImage2ImageDetail(Image image, Config config, ImageLinkGenerator imageLinkGenerator) {
        String ownerName = configService.getGithubAccount(config.getUserId());
        ImageDetail imageDetail = new ImageDetail();
        BeanUtils.copyProperties(image, imageDetail);
        imageDetail.setUrl(imageLinkGenerator.generate(ownerName, image.getPath()));
        return imageDetail;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#config.getUserId() + '-' + T(com.morningstar.util.TimeUtil).getCurrentLocalDate().format(T(java.time.format.DateTimeFormatter).ofPattern('yyyyMMdd'))", condition = "#result != null"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#config.userId", condition = "#result != null")
    })
    public ImageDetail uploadForMWeb(MultipartFile file, Config config, ImageLinkGenerator imageLinkGenerator) {
        String pat = config.getGithubPat();
        String oldFilename = file.getOriginalFilename();
        BufferedImage bufferedImage = ImageUtil.read(file);
        if (bufferedImage == null) {
            throw new PicImageFormatNotSupportedException(oldFilename);
        }

        byte[] content = null;
        String newFilename;
        if (config.getCompressionQuality() != null) {
            newFilename = Objects.requireNonNull(oldFilename).substring(0, oldFilename.lastIndexOf(".")) + ".webp";
            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(String.valueOf(CompressionType.Lossy));
            writeParam.setCompressionQuality(config.getCompressionQuality());
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(bufferedImage, null, null), writeParam);
                content = baos.toByteArray();
            } catch (IOException ignored) {
            } finally {
                writer.dispose();
            }
        } else {
            newFilename = oldFilename;
            try {
                content = file.getResource().getContentAsByteArray();
            } catch (IOException ignored) {
            }
        }

        JsonNode result = githubUtil.createFile(pat, ImageLinkGenerator.repoName, getFilePath(newFilename), content);
        Image image = parseFileNodeToImage(result.get("content"));
        if (image == null) {
            throw new PicImageUploadingException(oldFilename);
        }
        return convertImage2ImageDetail(image, config, imageLinkGenerator);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_IMAGES, key = "#config.getUserId() + '-' + #path.split(\"/\")[0]"),
            @CacheEvict(cacheNames = RedisConstant.CACHE_PIC_DIRS, key = "#config.userId", condition = "#result != null")
    })
    public void deleteImage(String path, Config config) {
        String pat = configService.getGithubPat(config.getUserId());
        try {
            githubUtil.deleteFile(pat, ImageLinkGenerator.repoName, path);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BaseException(String.format("文件[%s]不存在", path));
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> relayDownload(String ownerName, String dirName, String fileName) {
        if (!ImageUtil.isValidImageFilename(fileName)) {
            throw new PicImageInvalidFilenameException(fileName);
        }
        String url = String.format("https://raw.githubusercontent.com/%s/%s/main/%s/%s", ownerName, ImageLinkGenerator.repoName, dirName, fileName);
        ResponseEntity<Resource> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Resource.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
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
