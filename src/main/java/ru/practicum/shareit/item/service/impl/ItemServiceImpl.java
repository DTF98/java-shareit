package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.CreatingCommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Transactional
    public ItemDto add(ItemDto itemDto, Long userId) {
        getUserById(userId);
        Item item = itemMapper.toItemModel(itemDto, userId);
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    @Transactional(readOnly = true)
    public Collection<AdvancedItemDto> getAllByOwner(Long userId) {
        getUserById(userId);
        List<Item> listItems = itemRepository.findAllByOwnerId(userId);
        Set<Long> itemIds = listItems.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Comment>> commentsMapping = getItemCommentMapping(itemIds);
        Map<Long, List<Booking>> lastBookingMapping = bookingService.getItemLastBookingMapping(itemIds);
        Map<Long, List<Booking>> nextBookingMapping = bookingService.getItemNextBookingMapping(itemIds);
        return itemMapper.toExtendInfoDto(listItems, commentsMapping, lastBookingMapping, nextBookingMapping);
    }

    @Transactional(readOnly = true)
    public AdvancedItemDto getById(Long id, Long userId) {
        getUserById(userId);
        Item item = getItemById(id);
        Map<Long, List<Comment>> commentsMapping = getItemCommentMapping(Set.of(item.getId()));
        Map<Long, List<Booking>> lastBookingMapping = null;
        Map<Long, List<Booking>> nextBookingMapping = null;
        if (Objects.equals(userId, item.getOwnerId())) {
            lastBookingMapping = bookingService.getItemLastBookingMapping(Set.of(item.getId()));
            nextBookingMapping = bookingService.getItemNextBookingMapping(Set.of(item.getId()));
        }
        return itemMapper.toExtendInfoDto(List.of(item), commentsMapping, lastBookingMapping, nextBookingMapping)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Ошибка маппинга ItemWithExtendInfoDto"));
    }

    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        getUserById(userId);
        Item savedItem = getItemById(itemId);
        if (!Objects.equals(userId, savedItem.getOwnerId())) {
            throw new AccessException(String.format("Ошибка доступа, пользователь с id = %s, не может редактировать" +
                   " вещь по id = %s", userId, savedItem.getId()));
        }
        itemMapper.updateModel(savedItem, itemDto);
        return itemMapper.toItemDto(itemRepository.save(savedItem));
    }

    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, Long userId) {
        getUserById(userId);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text,
                text, true);
        return itemMapper.toListItemDto(items);
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = getUserById(userId);
        commentIsExist(userId, itemId);
        checkingUserForBookingItem(itemId, userId);
        return itemMapper.toCommentDto(commentRepository.save(itemMapper.toCommentModel(commentDto, user, itemId)));
    }

    @Transactional(readOnly = true)
    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь по id = %s не найден!", id)));
    }

    @Transactional(readOnly = true)
    private Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Вещь по id = %s не найдена!", id)));
    }

    @Transactional(readOnly = true)
    private void commentIsExist(Long userId, Long itemId) {
        Comment comment = commentRepository.findByAuthorIdAndItemId(userId, itemId);
        if (comment != null) {
            throw new ValidationException("Отзыв уже существует");
        }
    }

    private void checkingUserForBookingItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .findFirst()
                .orElseThrow(() ->
                        new CreatingCommentException("Пользователь не может оставить отзыв на предмет, который не бронировал"));
    }

    @Transactional(readOnly = true)
    private Map<Long, List<Comment>> getItemCommentMapping(Set<Long> itemIds) {
        Map<Long, List<Comment>> commentMapping;
        commentMapping = commentRepository.findAllByItemIdIn(itemIds)
                .stream().collect(Collectors.groupingBy(Comment::getItemId));
        return commentMapping;
    }
}
