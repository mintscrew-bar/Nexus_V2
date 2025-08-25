package com.nexus.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MultipleOfFiveValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleOfFive {
    String message() default "참가 인원은 5의 배수여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}