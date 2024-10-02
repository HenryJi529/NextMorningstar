package com.morningstar.validation.constraint;

import com.morningstar.validation.validator.FieldMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FieldMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMatch {
    String message() default "字段不匹配";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field1();

    String field2();
}
