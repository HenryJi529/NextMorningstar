package com.morningstar.validation.constraint;

import com.morningstar.validation.validator.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface Phone {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


