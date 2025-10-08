package com.github.jvalsesia.pcms.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {
    String message() default "Date must be in YYYY-MM-DD format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}