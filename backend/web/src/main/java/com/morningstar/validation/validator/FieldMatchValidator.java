package com.morningstar.validation.validator;

import com.morningstar.validation.constraint.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field1;
    private String field2;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field1 = constraintAnnotation.field1();
        this.field2 = constraintAnnotation.field2();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field field1 = value.getClass().getDeclaredField(this.field1);
            Field field2 = value.getClass().getDeclaredField(this.field2);
            field1.setAccessible(true);
            field2.setAccessible(true);
            Object field1Value = field1.get(value);
            Object field2Value = field2.get(value);
            if (field1Value == null || field2Value == null) {
                return true;
            }
            return Objects.equals(field1Value, field2Value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
