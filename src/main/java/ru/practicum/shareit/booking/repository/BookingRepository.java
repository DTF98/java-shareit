package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserAll(Long bookerId);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.start > :now ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserFuture(Long bookerId, LocalDateTime now);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.end < :now ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserPast(Long bookerId, LocalDateTime now);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.start < :now AND bk.end > :now ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserCurrent(Long bookerId, LocalDateTime now);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = ?1 AND bk.status = ?2 ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserByStatus(Long bookerId, BookingStatus status);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerAll(Long ownerId);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.start > :now ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerFuture(Long ownerId, LocalDateTime now);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.end < :now ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerPast(Long ownerId, LocalDateTime now);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.start < :now AND bk.end > :now ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerCurrent(Long ownerId, LocalDateTime now);

    @Query(value = "SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.status = :status ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerStatus(Long ownerId, BookingStatus status);

    @Query(value = "SELECT DISTINCT ON (item_id) bk.* FROM bookings bk WHERE bk.item_id IN :itemIds AND " +
            "(bk.date_end < :time OR bk.date_start < :time AND bk.date_end > :time) AND bk.status = :status " +
            "ORDER BY bk.date_end DESC", nativeQuery = true)
    List<Booking> findLastBookingWithStatus(Set<Long> itemIds, LocalDateTime time, String status);

    @Query(value = "SELECT DISTINCT ON (item_id) bk.* FROM bookings bk WHERE bk.item_id IN :itemIds AND " +
            "bk.date_start > :time AND bk.status = :status ORDER BY bk.date_start", nativeQuery = true)
    List<Booking> findNextBookingWithStatus(Set<Long> itemIds, LocalDateTime time, String status);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime dateTime);
}