package shareit.validation;

import shareit.booking.dto.create.CreateBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidBookingIntervalValidator implements ConstraintValidator<ValidBookingInterval, Object> {
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            CreateBookingDto createBookingDto = (CreateBookingDto) value;
            LocalDateTime start = createBookingDto.getStart();
            LocalDateTime end = createBookingDto.getEnd();
            LocalDateTime now = LocalDateTime.now();
            return end.isAfter(start) && end.isAfter(now) && start.isAfter(now);
        } catch (final Exception ignore) {
            // ignore
        }
        return false;
    }
}
