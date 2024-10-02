package com.morningstar.validation.validator;

import com.morningstar.validation.constraint.Set;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;

public class SetValidator implements ConstraintValidator<Set, List<?>> {
    @Override
    public boolean isValid(List<?> objects, ConstraintValidatorContext constraintValidatorContext) {
        if (objects == null) {
            return true;
        }
        return new HashSet<>(objects).size() == objects.size();
    }
}
