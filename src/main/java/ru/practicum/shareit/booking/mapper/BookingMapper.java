package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", source = "bookingDtoIn.id")
    Booking toModel(BookingDtoIn bookingDtoIn, User booker, Item item);

    BookingDtoOut toDto(Booking booking);

    List<BookingDtoOut> toDto(List<Booking> bookings);
}
