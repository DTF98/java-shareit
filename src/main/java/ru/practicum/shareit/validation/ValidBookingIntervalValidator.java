package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.create.CreateBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidBookingIntervalValidator implements ConstraintValidator<ValidBookingInterval, Object> {
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            CreateBookingDto bookingCreateDto = (CreateBookingDto) value;
            LocalDateTime start = bookingCreateDto.getStart();
            LocalDateTime end = bookingCreateDto.getEnd();
            LocalDateTime now = LocalDateTime.now();
            return end.isAfter(start) && end.isAfter(now) && start.isAfter(now);
        } catch (final Exception ignore) {
        }
        return true;
    }
}
