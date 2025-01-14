package com.example.apiecommerce.domain.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordCriteriaValidator.class)
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface PasswordCriteria {
    String message() default "The password must be at least 8 characters long, contain at least one lowercase letter, contain at least one uppercase letter, contain at least one digit, and contain at least one special character.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
