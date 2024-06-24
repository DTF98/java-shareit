package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDtoOut createBooking(Long userId, BookingDtoIn bookingDtoIn) {
        User user = getUserById(userId);
        Item item = getItemById(bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }
        if (Objects.equals(item.getOwner(), userId)) {
            throw new NotFoundException("Невозможно забронировать свой предмет");
        }
        Booking booking = bookingMapper.toModel(bookingDtoIn, user, item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Transactional
    public BookingDtoOut updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);
        if (!Objects.equals(userId, booking.getItem().getOwner())) {
            throw new NotFoundException("Ошибка обновления записи!");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования изменить уже нельзя");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingDtoOut getBooking(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);
        if (!Objects.equals(userId, booking.getBooker().getId()) &&
                !Objects.equals(userId, booking.getItem().getOwner())) {
            throw new NotFoundException("Ошибка просмотра записи!");
        }
        return bookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDtoOut> getBookingsForUser(Long userId, BookingState state) {
        getUserById(userId);
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForUserAll(userId);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForUserCurrent(userId, LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findBookingsForUserPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findBookingsForUserFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findBookingsForUserByStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findBookingsForUserByStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                log.warn("Неизвестное состояние записи = {}", state);
                return List.of();
        }
        return bookingMapper.toDto(result);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDtoOut> getBookingsForItemOwner(Long userId, BookingState state) {
        getUserById(userId);
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForItemOwnerAll(userId);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForItemOwnerCurrent(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findBookingsForItemOwnerPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findBookingsForItemOwnerFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findBookingsForItemOwnerStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findBookingsForItemOwnerStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                log.warn("Неизвестное состояние записи = {}", state);
                return List.of();
        }
        return bookingMapper.toDto(result);
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Запись по id = %s не найдена", bookingId)));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId, User.class).orElseThrow(
                ()-> new NotFoundException(String.format("Пользователь по id = %s не найден!", userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId, Item.class).orElseThrow(
                ()-> new NotFoundException(String.format("Вещь по id = %s не найдена!", itemId)));
    }
}
