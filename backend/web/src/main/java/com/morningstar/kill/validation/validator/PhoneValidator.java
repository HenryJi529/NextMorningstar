package com.morningstar.kill.validation.validator;

import com.morningstar.kill.validation.constraint.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 只验证存在的手机号
        if (value == null || value.isEmpty()) {
            return true;
        }
        // 匹配中国大陆的手机号码
        String regex = "^1[3-9]\\d{9}$";
        return value.matches(regex);
    }
}

