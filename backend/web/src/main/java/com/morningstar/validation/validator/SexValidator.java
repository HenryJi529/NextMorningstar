package com.morningstar.validation.validator;

import com.morningstar.validation.constraint.Sex;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SexValidator implements ConstraintValidator<Sex, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 不判断性别是否为空
        if (value == null || value.isEmpty()) {
            return true;
        }

        return value.equals("男") || value.equals("女");
    }
}
