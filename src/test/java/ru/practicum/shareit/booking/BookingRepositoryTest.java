package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User booker1;
    private User booker2;
    private User owner1;
    private User owner2;
    private LocalDateTime now;
    private Booking booking2;
    private Booking booking4;
    private Booking booking5;

    @BeforeEach
    void setUp() {
        booker1 = new User(null, "Booker 1", "booker1@mail.com");
        booker2 = new User(null, "Booker 2", "booker2@mail.com");
        owner1 = new User(null, "Owner 1", "owner1@mail.com");
        owner2 = new User(null, "Owner 2", "owner2@mail.com");
        userRepository.saveAll(List.of(booker1, booker2, owner1, owner2));

        Item item1 = new Item(null, "Item 1", "Item 1 desc", true, owner1.getId(), null);
        Item item2 = new Item(null, "Item 2", "Item 2 desc", true, owner2.getId(), null);
        itemRepository.saveAll(List.of(item1, item2));

        now = LocalDateTime.of(2010, 1, 1, 10, 0);
        Booking booking1 = new Booking(null, item1, BookingStatus.APPROVED, booker1, now.minusYears(2), now.minusYears(2).plusDays(1));
        booking2 = new Booking(null, item2, BookingStatus.APPROVED, booker1, now.minusYears(4), now.minusYears(4).plusDays(1));
        Booking booking3 = new Booking(null, item1, BookingStatus.APPROVED, booker1, now.minusYears(1), now.minusYears(1).plusDays(1));
        booking4 = new Booking(null, item1, BookingStatus.REJECTED, booker1, now.plusYears(3), now.plusYears(3).plusDays(1));
        booking5 = new Booking(null, item2, BookingStatus.WAITING, booker1, now.plusYears(2), now.plusYears(2).plusDays(1));
        Booking booking6 = new Booking(null, item1, BookingStatus.APPROVED, booker1, now.plusYears(5), now.plusYears(5).plusDays(1));
        Booking booking7 = new Booking(null, item1, BookingStatus.APPROVED, booker2, now.plusYears(6), now.plusYears(6).plusDays(1));
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4, booking5, booking6, booking7));
    }

    @Test
    void findBookingsForUserAllWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsBooker1 = bookingRepository.findBookingsForUserAll(booker1.getId(), page);
        assertThat(bookingsBooker1, hasSize(6));
        final List<Booking> bookingsBooker2 = bookingRepository.findBookingsForUserAll(booker2.getId(), page);
        assertThat(bookingsBooker2, hasSize(1));
    }

    @Test
    void findBookingsForUserFutureWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsBooker1 = bookingRepository.findBookingsForUserFuture(booker1.getId(), now, page);
        assertThat(bookingsBooker1, hasSize(3));
        final List<Booking> bookingsBooker2 = bookingRepository.findBookingsForUserFuture(booker2.getId(), now, page);
        assertThat(bookingsBooker2, hasSize(1));
    }

    @Test
    void findBookingsForUserPastWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsBooker1 = bookingRepository.findBookingsForUserPast(booker1.getId(), now, page);
        assertThat(bookingsBooker1, hasSize(3));
        final List<Booking> bookingsBooker2 = bookingRepository.findBookingsForUserPast(booker2.getId(), now, page);
        assertThat(bookingsBooker2, hasSize(0));
    }

    @Test
    void findBookingsForUserCurrentWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        LocalDateTime dateTime = booking4.getStart().plusSeconds(1);
        final List<Booking> bookingsBooker1 = bookingRepository.findBookingsForUserCurrent(booker1.getId(), dateTime, page);
        assertThat(bookingsBooker1, hasSize(1));
        assertThat(bookingsBooker1, contains(booking4));
        final List<Booking> bookingsBooker2 = bookingRepository.findBookingsForUserCurrent(booker2.getId(), dateTime, page);
        assertThat(bookingsBooker2, hasSize(0));
    }

    @Test
    void findBookingsForUserByStatusWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsBooker1 = bookingRepository.findBookingsForUserByStatus(booker1.getId(), BookingStatus.APPROVED, page);
        assertThat(bookingsBooker1, hasSize(4));
        final List<Booking> bookingsBooker1NotFound = bookingRepository.findBookingsForUserByStatus(booker1.getId(), BookingStatus.REJECTED, page);
        assertThat(bookingsBooker1NotFound, hasSize(1));
        final List<Booking> bookingsBooker2 = bookingRepository.findBookingsForUserByStatus(booker2.getId(), BookingStatus.APPROVED, page);
        assertThat(bookingsBooker2, hasSize(1));
    }

    @Test
    void findBookingsForItemOwnerAllWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsOwner1 = bookingRepository.findBookingsForItemOwnerAll(owner1.getId(), page);
        assertThat(bookingsOwner1, hasSize(5));
        final List<Booking> bookingsOwner2 = bookingRepository.findBookingsForItemOwnerAll(owner2.getId(), page);
        assertThat(bookingsOwner2, hasSize(2));
    }

    @Test
    void findBookingsForItemOwnerFutureWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsOwner1 = bookingRepository.findBookingsForItemOwnerFuture(owner1.getId(), now, page);
        assertThat(bookingsOwner1, hasSize(3));
        final List<Booking> bookingsOwner2 = bookingRepository.findBookingsForItemOwnerFuture(owner2.getId(), now, page);
        assertThat(bookingsOwner2, hasSize(1));
        assertThat(bookingsOwner2, contains(booking5));
    }

    @Test
    void findBookingsForItemOwnerPastWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsOwner1 = bookingRepository.findBookingsForItemOwnerPast(owner1.getId(), now, page);
        assertThat(bookingsOwner1, hasSize(2));
        final List<Booking> bookingsOwner2 = bookingRepository.findBookingsForItemOwnerPast(owner2.getId(), now, page);
        assertThat(bookingsOwner2, hasSize(1));
        assertThat(bookingsOwner2, contains(booking2));
    }

    @Test
    void findBookingsForItemOwnerCurrentWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        LocalDateTime dateTime = booking5.getStart().plusSeconds(1);
        final List<Booking> bookingsOwner1 = bookingRepository.findBookingsForItemOwnerCurrent(owner1.getId(), dateTime, page);
        assertThat(bookingsOwner1, hasSize(0));
        final List<Booking> bookingsOwner2 = bookingRepository.findBookingsForItemOwnerCurrent(owner2.getId(), dateTime, page);
        assertThat(bookingsOwner2, hasSize(1));
        assertThat(bookingsOwner2, contains(booking5));
    }

    @Test
    void findBookingsForItemOwnerStatusWithValidParametersReturnsAnswer() {
        Pageable page = Pagination.getPage(0, 10);
        final List<Booking> bookingsOwner1 = bookingRepository.findBookingsForItemOwnerStatus(owner1.getId(), BookingStatus.APPROVED, page);
        assertThat(bookingsOwner1, hasSize(4));
        final List<Booking> bookingsOwner1NotFound = bookingRepository.findBookingsForItemOwnerStatus(owner1.getId(), BookingStatus.WAITING, page);
        assertThat(bookingsOwner1NotFound, hasSize(0));
        final List<Booking> bookingsOwner2 = bookingRepository.findBookingsForItemOwnerStatus(owner2.getId(), BookingStatus.APPROVED, page);
        assertThat(bookingsOwner2, hasSize(1));
        assertThat(bookingsOwner2, contains(booking2));
    }
}
