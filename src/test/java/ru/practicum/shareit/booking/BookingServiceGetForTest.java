package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.create.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceGetForTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    private User booker;
    private User owner1;
    private User owner2;
    private User userWithoutBookings;
    private BookingDto pastBooking;
    private BookingDto futureBooking;
    private BookingDto currentBooking;
    private BookingDto futureRejectedBooking;

    @BeforeAll
    void setUp() {
        userWithoutBookings = userRepository.save(new User(null, "User without bookings", "nobookings@mail.com"));
        booker = userRepository.save(new User(null, "User", "user@mail.com"));
        owner1 = userRepository.save(new User(null, "Owner1", "owner1@mail.com"));
        owner2 = userRepository.save(new User(null, "Owner2", "owner2@mail.com"));
        Item itemByOwner1 = itemRepository.save(new Item(null, "Item", "Item desc", true, owner1.getId(), null));
        Item itemByOwner2 = itemRepository.save(new Item(null, "Item", "Item desc", true, owner2.getId(), null));

        pastBooking = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, itemByOwner1.getId(), null, LocalDateTime.now().minusYears(1), LocalDateTime.now().minusYears(1).plusDays(1)));
        bookingService.updateBookingStatus(itemByOwner1.getOwnerId(), pastBooking.getId(), true);

        futureBooking = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, itemByOwner1.getId(), null, LocalDateTime.now().plusYears(1), LocalDateTime.now().plusYears(1).plusDays(1)));
        bookingService.updateBookingStatus(itemByOwner1.getOwnerId(), futureBooking.getId(), true);

        currentBooking = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, itemByOwner1.getId(), null, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));

        futureRejectedBooking = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, itemByOwner2.getId(), null, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(10).plusDays(1)));
        bookingService.updateBookingStatus(itemByOwner2.getOwnerId(), futureRejectedBooking.getId(), false);
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("argumentsForGetBookingForUser")
    void getBookingsForUserWithValidParametersReturnsAnswerAndInvalidParametersReturnsEmptyList(Long userId, BookingState state, List<BookingDto> expected) {
        final int from = 0;
        final int size = 10;
        Set<Long> bookingIds = bookingService.getBookingsForUser(userId, state, from, size).stream().map(BookingDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Long> expectedIds = expected.stream().map(BookingDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        assertEquals(expectedIds, bookingIds);
    }

    Stream<Arguments> argumentsForGetBookingForUser() {
        return Stream.of(
                Arguments.of(booker.getId(), BookingState.ALL, List.of(futureBooking, futureRejectedBooking, currentBooking, pastBooking)),
                Arguments.of(booker.getId(), BookingState.CURRENT, List.of(currentBooking)),
                Arguments.of(booker.getId(), BookingState.PAST, List.of(pastBooking)),
                Arguments.of(booker.getId(), BookingState.FUTURE, List.of(futureBooking, futureRejectedBooking)),
                Arguments.of(booker.getId(), BookingState.WAITING, List.of(currentBooking)),
                Arguments.of(booker.getId(), BookingState.REJECTED, List.of(futureRejectedBooking)),
                Arguments.of(booker.getId(), BookingState.UNKNOWN, List.of()),
                Arguments.of(userWithoutBookings.getId(), BookingState.ALL, List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForGetBookingForItemOwner")
    void getBookingsForItemOwnerWithValidParametersReturnsAnswerAndInvalidParametersReturnsEmptyList(Long userId, BookingState state, List<BookingDto> expected) {
        final int from = 0;
        final int size = 10;
        Set<Long> bookingIds = bookingService.getBookingsForItemOwner(userId, state, from, size).stream().map(BookingDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Long> expectedIds = expected.stream().map(BookingDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        assertEquals(expectedIds, bookingIds);
    }

    Stream<Arguments> argumentsForGetBookingForItemOwner() {
        return Stream.of(
                Arguments.of(owner1.getId(), BookingState.ALL, List.of(futureBooking, currentBooking, pastBooking)),
                Arguments.of(owner1.getId(), BookingState.CURRENT, List.of(currentBooking)),
                Arguments.of(owner1.getId(), BookingState.PAST, List.of(pastBooking)),
                Arguments.of(owner1.getId(), BookingState.FUTURE, List.of(futureBooking)),
                Arguments.of(owner1.getId(), BookingState.WAITING, List.of(currentBooking)),
                Arguments.of(owner1.getId(), BookingState.REJECTED, List.of()),
                Arguments.of(owner1.getId(), BookingState.UNKNOWN, List.of()),

                Arguments.of(owner2.getId(), BookingState.REJECTED, List.of(futureRejectedBooking))
        );
    }
}
