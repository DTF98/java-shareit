package shareit.booking.dto.create;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import shareit.validation.ValidBookingInterval;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ValidBookingInterval(message = "Некорректное время бронирования")
public class CreateBookingDto {
    @NotNull(message = "Id предмета не может отсутствовать")
    Long itemId;

    @NotNull(message = "Дата начала бронирования не может отсутствовать")
    @FutureOrPresent
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может отсутствовать")
    @FutureOrPresent
    LocalDateTime end;
}
