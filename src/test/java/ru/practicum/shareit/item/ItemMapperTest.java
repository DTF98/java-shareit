package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemMapperTest {
    private static ItemMapper itemMapper;

    @BeforeAll
    static void beforeAll() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    void toExtendInfoDtoWithValidParametersReturnsAnswer() {
        Item item1 = new Item(1L, "Item 1", "Item 1 desc", true, 10L, null);
        Item item2 = new Item(2L, "Item 2", "Item 2 desc", true, 10L, null);
        Item item3 = new Item(3L, "Item 3", "Item 3 desc", true, 10L, null);
        Item item4 = new Item(4L, "Item 4", "Item 4 desc", true, 10L, null);
        List<Item> items = List.of(item1, item2, item3, item4);

        Comment comment = new Comment(1L, item2.getId(), null, "text", LocalDateTime.now());
        Map<Long, List<Comment>> commentMapping = new HashMap<>();
        commentMapping.put(2L, List.of(comment));

        Booking booking = new Booking(1L, item3, BookingStatus.APPROVED, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Map<Long, List<Booking>> lastBookingMapping = new HashMap<>();
        lastBookingMapping.put(3L, List.of(booking));

        Map<Long, List<Booking>> nextBookingMapping = new HashMap<>();

        List<AdvancedItemDto> extendInfoDto = itemMapper.toExtendInfoDto(items, commentMapping, lastBookingMapping, nextBookingMapping);

        assertThat(extendInfoDto, hasSize(4));
        assertThat(extendInfoDto.stream().filter(i -> i.getId() == 1L).findFirst().orElseThrow().getComments(), hasSize(0));
        assertThat(extendInfoDto.stream().filter(i -> i.getId() == 2L).findFirst().orElseThrow().getComments()
                .stream().map(CommentDto::getId).collect(Collectors.toSet()), contains(comment.getId()));
        assertThat(extendInfoDto.stream().filter(i -> i.getId() == 3L).findFirst().orElseThrow().getLastBooking().getId(), equalTo(booking.getId()));
        assertThat(extendInfoDto.stream().filter(i -> i.getId() == 3L).findFirst().orElseThrow().getNextBooking(), nullValue());
    }
}
