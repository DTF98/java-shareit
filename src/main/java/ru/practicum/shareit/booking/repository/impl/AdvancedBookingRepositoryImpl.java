package ru.practicum.shareit.booking.repository.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.AdvancedBookingRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class AdvancedBookingRepositoryImpl implements AdvancedBookingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Booking> findLastBookingWithStatus(Set<Long> itemIds, LocalDateTime time,
                                                   BookingStatus status) {
        String sql = "SELECT DISTINCT ON (item_id) bk.* " +
                "FROM bookings bk " +
                "WHERE bk.item_id IN :itemIds AND (bk.date_end < :dateTime OR bk.date_start < :dateTime AND bk.date_end " +
                "> :dateTime) AND bk.status = :status " +
                "ORDER BY bk.date_end DESC";
        return entityManager.createNativeQuery(sql, Booking.class)
                .setParameter("itemIds", itemIds)
                .setParameter("dateTime", time)
                .setParameter("dateTime", time)
                .setParameter("dateTime", time)
                .setParameter("status", status.toString())
                .getResultList();
    }

    @Override
    public List<Booking> findNextBookingWithStatus(Set<Long> itemIds, LocalDateTime time,
                                                   BookingStatus status) {
        String sql = "SELECT DISTINCT ON (item_id) bk.* " +
                "FROM bookings bk " +
                "WHERE bk.item_id IN :itemIds AND bk.date_start > :time AND bk.status = :status " +
                "ORDER BY bk.date_start";
        return entityManager.createNativeQuery(sql, Booking.class)
                .setParameter("itemIds", itemIds)
                .setParameter("time", time)
                .setParameter("status", status.toString())
                .getResultList();
    }
}
