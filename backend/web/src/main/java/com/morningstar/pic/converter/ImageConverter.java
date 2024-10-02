package com.morningstar.pic.converter;

import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.ImageDetail;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageConverter {
    @Mapping(target = "url", ignore = true)
    @BeanMapping(builder = @org.mapstruct.Builder(disableBuilder = true))
    ImageDetail imageToImageDetail(Image image);
}
