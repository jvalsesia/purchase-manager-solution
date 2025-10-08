package com.github.jvalsesia.pcms.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // If null, let @NotNull handle it
        return true; // LocalDate is already validated by Jackson
    }
}