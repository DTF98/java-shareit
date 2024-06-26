package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.AdvancedBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", source = "createBookingDto.id")
    Booking toModel(CreateBookingDto createBookingDto, User booker, Item item);

    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);

    @Mapping(target = "itemId", source = "booking.item.id")
    @Mapping(target = "bookerId", source = "booking.booker.id")
    AdvancedBookingDto toAdvancedBookingDto(Booking booking);
}
