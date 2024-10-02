package com.morningstar.validation.constraint;

import com.morningstar.validation.validator.SetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SetValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Set {
    String message() default "元素必须唯一";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
