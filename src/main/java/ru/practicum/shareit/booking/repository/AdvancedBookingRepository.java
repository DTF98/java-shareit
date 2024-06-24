package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface AdvancedBookingRepository {

    List<Booking> findLastBookingWithStatus(Set<Long> itemIds, LocalDateTime now, BookingStatus status);

    List<Booking> findNextBookingWithStatus(Set<Long> itemIds, LocalDateTime now, BookingStatus status);
}
