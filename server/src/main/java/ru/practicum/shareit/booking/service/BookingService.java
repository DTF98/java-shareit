package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookingService {
    BookingDto createBooking(Long userId, CreateBookingDto createBookingDto);

    BookingDto updateBookingStatus(Long userId, Long bookingId, boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getBookingsForUser(Long userId, BookingState state, int from, int size);

    Collection<BookingDto> getBookingsForItemOwner(Long userId, BookingState bookingState, int from, int size);

    Map<Long, List<Booking>> getItemLastBookingMapping(Set<Long> itemIds);

    Map<Long, List<Booking>> getItemNextBookingMapping(Set<Long> itemIds);
}
