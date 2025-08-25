package com.nexus.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MultipleOfFiveValidator implements ConstraintValidator<MultipleOfFive, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; 
        }
        return value % 5 == 0;
    }
}