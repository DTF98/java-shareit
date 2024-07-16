package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNKNOWN;

    public static BookingState parseState(String str) {
        BookingState bookingState;
        if (str == null) {
            bookingState = BookingState.ALL;
        } else {
            try {
                bookingState = BookingState.valueOf(str);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Unknown state: %s", str));
            }
        }
        return bookingState;
    }
}
