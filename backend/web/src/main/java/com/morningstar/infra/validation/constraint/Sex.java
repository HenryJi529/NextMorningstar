package com.morningstar.infra.validation.constraint;

import com.morningstar.infra.validation.validator.SexValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SexValidator.class)
@Documented
public @interface Sex {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

