package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validation.ValidBookingInterval;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ValidBookingInterval(message = "Некорректное время бронирования")
public class CreateBookingDto {
    @EqualsAndHashCode.Include
    @Null
    Long id;

    @NotNull(message = "Необходим id предмета!")
    Long itemId;

    @Null
    BookingStatus status;

    @NotNull(message = "Необходима дата начала бронирования!")
    @FutureOrPresent
    LocalDateTime start;

    @NotNull(message = "Необходима дата окончания бронирования!")
    @FutureOrPresent
    LocalDateTime end;
}
