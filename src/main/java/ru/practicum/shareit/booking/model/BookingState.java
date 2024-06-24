package ru.practicum.shareit.booking.model;

import javax.validation.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState parseState(String str) {
        BookingState bookingState;
        if (str == null) {
            bookingState = BookingState.ALL;
        } else {
            try {
                bookingState = BookingState.valueOf(str);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Неизвестное состояние: %s", str));
            }
        }
        return bookingState;
    }
}
