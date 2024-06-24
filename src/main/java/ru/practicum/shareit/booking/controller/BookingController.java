package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании bookingId={}, userId={}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoOut> getBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(required = false) String state) {
        log.info("Получение списка бронирований пользователя userId={}, по состоянию = {}", userId, state);
        return bookingService.getBookingsForUser(userId, BookingState.parseState(state));
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOut> getBookingsByStateOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(required = false) String state) {
        log.info("Получение списка бронирований владельцем userId={}, по состоянию = {}", userId, state);
        return bookingService.getBookingsForItemOwner(userId, BookingState.parseState(state));
    }

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        log.info("Создание бронирования booking={}, userId={}", bookingDtoIn, userId);
        return bookingService.createBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam boolean approved) {
        log.info("Обновлении статуса бронировании userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }
}