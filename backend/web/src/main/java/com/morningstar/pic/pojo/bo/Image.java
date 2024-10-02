package com.morningstar.pic.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    private String filename;

    private String path;

    private Long size;

    private LocalDateTime updateTime;
}