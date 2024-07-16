package shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shareit.booking.dto.create.CreateBookingDto;
import shareit.booking.model.BookingState;
import shareit.booking.client.BookingClient;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public Object getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании bookingId={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public Object getBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false) String state,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение списка бронирований пользователя userId={}, по состоянию = {}", userId, state);
        return bookingClient.getBookingsByState(userId, BookingState.parseState(state), from, size);
    }

    @GetMapping("/owner")
    public Object getBookingsByStateOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(required = false) String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение списка бронирований владельцем userId={}, по состоянию = {}", userId, state);
        return bookingClient.getBookingsByStateOwner(userId, BookingState.parseState(state), from, size);
    }

    @PostMapping
    public Object createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid CreateBookingDto createBookingDto) {
        log.info("Создание бронирования booking={}, userId={}", createBookingDto, userId);
        return bookingClient.createBooking(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Object updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Обновлении статуса бронировании userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }
}
