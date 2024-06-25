package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NoChangeStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableForBookingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(Long userId, CreateBookingDto createBookingDto) {
        User user = getUserById(userId);
        Item item = getItemById(createBookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new UnavailableForBookingException("Предмет недоступен для бронирования");
        }
        if (Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException("Невозможно забронировать свой предмет");
        }
        Booking booking = bookingMapper.toModel(createBookingDto, user, item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Transactional
    public BookingDto updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);
        if (!Objects.equals(userId, booking.getItem().getOwnerId())) {
            throw new NotFoundException("Ошибка обновления записи!");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new NoChangeStatusException("Статус бронирования изменить уже нельзя");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingDto getBooking(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);
        if (!Objects.equals(userId, booking.getBooker().getId()) &&
                !Objects.equals(userId, booking.getItem().getOwnerId())) {
            throw new NotFoundException("Ошибка просмотра записи!");
        }
        return bookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsForUser(Long userId, BookingState state) {
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
                log.warn("Unknown state: {}", state);
                return List.of();
        }
        return bookingMapper.toDto(result);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsForItemOwner(Long userId, BookingState state) {
        getUserById(userId);
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForItemOwnerAll(userId);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForItemOwnerCurrent(userId, LocalDateTime.now());
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
                log.warn("Unknown state: {}", state);
                return List.of();
        }
        return bookingMapper.toDto(result);
    }

    @Override
    public Map<Long, List<Booking>> getItemLastBookingMapping(Set<Long> itemIds) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> lastBookingMapping;
        lastBookingMapping = bookingRepository
                .findLastBookingWithStatus(itemIds, now, BookingStatus.APPROVED.toString())
                .stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        return lastBookingMapping;
    }

    @Override
    public Map<Long, List<Booking>> getItemNextBookingMapping(Set<Long> itemIds) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> nextBookingMapping;
        nextBookingMapping = bookingRepository
                .findNextBookingWithStatus(itemIds, now, BookingStatus.APPROVED.toString()).stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        return nextBookingMapping;
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Запись по id = %s не найдена", bookingId)));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь по id = %s не найден!", userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Вещь по id = %s не найдена!", itemId)));
    }
}
