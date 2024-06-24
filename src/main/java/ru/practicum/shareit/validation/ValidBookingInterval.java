package ru.practicum.shareit.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ValidBookingIntervalValidator.class}
)
public @interface ValidBookingInterval {
    String message() default "Date interval validation failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
