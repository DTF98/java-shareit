package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder
public class BookingDtoOut {

    Long id;

    ItemDto item;

    UserDto booker;

    BookingStatus status;

    LocalDateTime start;

    LocalDateTime end;
}
