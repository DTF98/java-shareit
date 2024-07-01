package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingState;
import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingParseStateTest {

    @Test
    void parseStateWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() {
        assertEquals(BookingState.ALL, BookingState.parseState("ALL"));
        assertEquals(BookingState.ALL, BookingState.parseState(null));
        assertEquals(BookingState.FUTURE, BookingState.parseState("FUTURE"));
        assertThrows(ValidationException.class, () -> BookingState.parseState("unknown state"));
    }
}
