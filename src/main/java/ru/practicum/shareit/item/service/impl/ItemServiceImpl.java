package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public ItemDto add(ItemDto itemDto, Long userId) {
        getUserById(userId);
        Item item = itemMapper.toModel(itemDto, userId);
        Item saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Collection<ItemDto> getAllByOwner(Long userId) {
        getUserById(userId);
        List<Item> listItems = itemRepository.findAllByOwnerId(userId, Item.class);
        return itemMapper.collectionToDto(listItems);
    }

    @Transactional(readOnly = true)
    public ItemDto getById(Long id, Long userId) {
        getUserById(userId);
        Item item = getItemById(id);
        return itemMapper.toDto(item);
    }

    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        getUserById(userId);
        Item savedItem = getItemById(itemId);
        if (!Objects.equals(userId, savedItem.getOwner())) {
            throw new AccessException(String.format("Ошибка доступа, пользователь с id = %s, не может редактировать" +
                   " вещь по id = %s", userId, savedItem.getId()));
        }
        itemMapper.updateModel(savedItem, itemDto);
        return itemMapper.toDto(itemRepository.save(savedItem));
    }

    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, Long userId) {
        getUserById(userId);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text,
                text, true, ItemDto.class);
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = getUserById(userId);
        commentIsExist(userId, itemId);
        checkingUserForBookingItem(itemId, userId);
        return commentMapper.toDto(commentRepository.save(commentMapper.toModel(commentDto, user, itemId)));
    }

    @Transactional(readOnly = true)
    private User getUserById(Long id) {
        return userRepository.findById(id, User.class).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь по id = %s не найден!", id)));
    }

    @Transactional(readOnly = true)
    private Item getItemById(Long id) {
        return itemRepository.findById(id, Item.class).orElseThrow(
                ()-> new NotFoundException(String.format("Вещь по id = %s не найдена!", id)));
    }

    @Transactional(readOnly = true)
    private void commentIsExist(Long userId, Long itemId) {
        Comment comment = commentRepository.findByAuthorIdAndItemId(userId, itemId, Comment.class);
        if (comment != null) {
            throw new ValidationException("Отзыв уже существует");
        }
    }

    private void checkingUserForBookingItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        bookings.stream().
                filter(b -> b.getStatus() == BookingStatus.APPROVED).
                findFirst().
                orElseThrow(() ->
                        new ValidationException("Пользователь не может оставить отзыв на предмет, который не бронировал"));
    }
}
