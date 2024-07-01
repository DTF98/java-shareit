package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.create.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NoChangeStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableForBookingException;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({BookingMapperImpl.class, ItemMapperImpl.class, UserMapperImpl.class})
public class BookingServiceImplTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    private User booker;
    private User owner;
    private User otherUser;
    private Item item;

    @BeforeAll
    void setUp() {
        booker = userRepository.save(new User(null, "User", "user@mail.com"));
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        otherUser = userRepository.save(new User(null, "Other", "other@mail.com"));
        item = itemRepository.save(new Item(null, "Item", "Item desc", true, owner.getId(), null));
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void shouldNotCreateBookingForNotAvailableItemAndForOwnItem() {
        Item notAvailableItem = itemRepository.save(new Item(null, "Item", "Item desc", false, owner.getId(), null));
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        assertThrows(UnavailableForBookingException.class, () -> bookingService.createBooking(booker.getId(),
                new CreateBookingDto(null, notAvailableItem.getId(),null, start, end)));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(owner.getId(),
                new CreateBookingDto(null, item.getId(),null, start, end)));
    }

    @ParameterizedTest
    @MethodSource("argumentsForCorrectUser")
    void onlyCorrectUserShouldSeeBooking(Long userId, boolean canFetch) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookingDto booking = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, item.getId(),null, start, end));

        boolean ableToFetch;
        try {
            BookingDto fetched = bookingService.getBooking(userId, booking.getId());
            assertThat(fetched, equalTo(booking));
            ableToFetch = true;
        } catch (NotFoundException e) {
            ableToFetch = false;
        }
        assertThat(ableToFetch, equalTo(canFetch));
    }

    private Stream<Arguments> argumentsForCorrectUser() {
        return Stream.of(
                Arguments.of(owner.getId(), true),
                Arguments.of(booker.getId(), true),
                Arguments.of(otherUser.getId(), false)
        );
    }

    @Test
    void updateBookingStatusWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingDto bookingToApprove = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, item.getId(),null, start, end));
        assertThat(bookingToApprove.getStatus(), equalTo(BookingStatus.WAITING));
        BookingDto updatedBookingApproved = bookingService.updateBookingStatus(owner.getId(), bookingToApprove.getId(), true);
        assertThat(updatedBookingApproved.getStatus(), equalTo(BookingStatus.APPROVED));

        BookingDto bookingToReject = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, item.getId(),null, start, end));
        assertThat(bookingToReject.getStatus(), equalTo(BookingStatus.WAITING));
        BookingDto updatedBookingRejected = bookingService.updateBookingStatus(owner.getId(), bookingToReject.getId(), false);
        assertThat(updatedBookingRejected.getStatus(), equalTo(BookingStatus.REJECTED));


        BookingDto bookingToDoubleUpdate = bookingService.createBooking(booker.getId(), new CreateBookingDto(null, item.getId(), null, LocalDateTime.now().minusYears(1), LocalDateTime.now().minusYears(1).plusDays(1)));
        assertThat(bookingToDoubleUpdate.getStatus(), equalTo(BookingStatus.WAITING));
        // update by not owner
        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(otherUser.getId(), bookingToDoubleUpdate.getId(), true));

        bookingService.updateBookingStatus(owner.getId(), bookingToDoubleUpdate.getId(), false);
        assertThrows(NoChangeStatusException.class, () -> bookingService.updateBookingStatus(owner.getId(), bookingToDoubleUpdate.getId(), true));

    }


}
