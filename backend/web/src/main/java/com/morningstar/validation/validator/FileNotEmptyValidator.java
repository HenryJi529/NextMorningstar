package com.morningstar.validation.validator;

import com.morningstar.validation.constraint.FileNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        // 如果值为 null 或者文件内容为空，则校验失败
        if (value == null || value.isEmpty()) {
            return false;
        }
        return true;
    }
}