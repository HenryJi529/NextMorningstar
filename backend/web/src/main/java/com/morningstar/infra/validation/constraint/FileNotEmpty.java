package com.morningstar.infra.validation.constraint;

import com.morningstar.infra.validation.validator.FileNotEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileNotEmptyValidator.class)
@Documented
public @interface FileNotEmpty {
    String message() default "文件不能为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
