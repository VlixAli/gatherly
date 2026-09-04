package com.VlixAli.paleo.annotation;

import com.VlixAli.paleo.validator.NullOrNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {
    String message() default "must be null or not blank";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
