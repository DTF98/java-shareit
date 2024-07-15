package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.CreatingCommentException;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Test
    void shouldCreateAndGetAndUpdateItemWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() {
        User otherUser = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        User owner = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
        ItemRequest request = requestRepository.save(new ItemRequest(null, "desc", null, owner.getId()));
        ItemDto item = itemService.add(new ItemDto(null,"Item name", "Item desc", true, request.getId()), owner.getId());
        Item savedItem = itemRepository.findById(item.getId()).orElseThrow();

        assertEquals(itemMapper.toItemDto(savedItem), item);
        AdvancedItemDto saved = itemMapper.toExtendInfoDto(List.of(savedItem), null, null, null).get(0);
        assertEquals(saved, itemService.getById(item.getId(), owner.getId()));
        List<AdvancedItemDto> receivedItem = new ArrayList<>(itemService.getAllByOwner(owner.getId(), 0, 10));
        AdvancedItemDto itemForEquals = itemMapper.toExtendInfoDto(List.of(itemMapper.toItemModel(item, owner.getId())), null, null, null).get(0);
        assertEquals(receivedItem.get(0), itemForEquals);

        ItemDto toUpdate = new ItemDto(null, null, "Updated desc", true, null);
        itemService.update(toUpdate, item.getId(), owner.getId());
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();

        assertEquals("Item name", updatedItem.getName());
        assertEquals("Updated desc", updatedItem.getDescription());
        assertThrows(AccessException.class, () -> itemService.update(toUpdate, item.getId(), otherUser.getId()));
    }

    @Test
    void shouldGetCorrectMappings() {
        User booker = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        User owner = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
        ItemRequest request = requestRepository.save(new ItemRequest(null, "desc", null, owner.getId()));
        Item item = itemMapper.toItemModel(itemService.add(new ItemDto(null,"Item name", "Item desc", true, request.getId()), owner.getId()), owner.getId());
        bookingRepository.save(new Booking(null, item, BookingStatus.APPROVED, booker, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)));
        Comment comment = commentRepository.save(new Comment(null, item.getId(), booker, "text", null));

        Map<Long, List<Comment>> itemCommentMapping = itemService.getItemCommentMapping(Set.of(item.getId()));

        assertThat(itemCommentMapping.entrySet(), hasSize(1));
        assertThat(itemCommentMapping.get(item.getId()), equalTo(List.of(comment)));
    }

    @Test
    void shouldCreateCommentByCorrectUsers() {
        User booker = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        User owner = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
        Item item = itemMapper.toItemModel(itemService.add(new ItemDto(null,"Item name", "Item desc", true, null), owner.getId()), owner.getId());
        bookingRepository.save(new Booking(null, item, BookingStatus.APPROVED, booker, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)));

        assertThrows(CreatingCommentException.class, () -> itemService.createComment(new CommentDto(null,null,"text", null, null), item.getId(), owner.getId()));
        CommentDto comment = itemService.createComment(new CommentDto(null,null,"text", null, null), item.getId(), booker.getId());
        CommentDto savedComment = itemMapper.toCommentDto(commentRepository.findById(comment.getId()).orElseThrow());
        assertEquals(savedComment, comment);
    }
}
