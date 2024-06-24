package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDtoOut createBooking(Long userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut updateBookingStatus(Long userId, Long bookingId, boolean approved);

    BookingDtoOut getBooking(Long userId, Long bookingId);

    Collection<BookingDtoOut> getBookingsForUser(Long userId, BookingState bookingState);

    Collection<BookingDtoOut> getBookingsForItemOwner(Long userId, BookingState bookingState);
}
