package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AdvancedRequestItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;


public class ItemRequestMapperTest {
    private static ItemRequestMapper requestMapper;

    @BeforeAll
    static void beforeAll() {
        requestMapper = new ItemRequestMapperImpl();
    }

    @Test
    void shouldRequestsHaveCorrectItemsWithValidParametersReturnsAnswer() {
        ItemRequest request1 = new ItemRequest(1L, "desc 1", LocalDateTime.now(), 5L);
        ItemRequest request2 = new ItemRequest(2L, "desc 2", LocalDateTime.now(), 5L);
        ItemRequest request3 = new ItemRequest(3L, "desc 2", LocalDateTime.now(), 5L);
        Item item1 = new Item(4L, "Item 1", "Item desc 1", true, 10L, request1.getId());
        Item item2 = new Item(5L, "Item 2", "Item desc 2", true, 10L, request2.getId());
        Item item3 = new Item(6L, "Item 3", "Item desc 3", true, 10L, request2.getId());

        Map<Long, List<Item>> items = new HashMap<>();
        items.put(1L, List.of(item1));
        items.put(2L, List.of(item2, item3));

        List<AdvancedRequestItemDto> requestWithItemInfoDto = requestMapper
                .toListAdvancedItemRequestDto(List.of(request1, request2, request3), items);

        assertThat(requestWithItemInfoDto, hasSize(3));
        assertThat(requestWithItemInfoDto.stream()
                .filter(i -> Objects.equals(i.getId(), request1.getId()))
                .findFirst()
                .orElseThrow()
                .getItems(), hasSize(1));
        assertThat(requestWithItemInfoDto.stream()
                .filter(i -> Objects.equals(i.getId(), request2.getId()))
                .findFirst().orElseThrow()
                .getItems(), hasSize(2));
        assertThat(requestWithItemInfoDto.stream()
                .filter(i -> Objects.equals(i.getId(), request3.getId()))
                .findFirst().orElseThrow()
                .getItems(), hasSize(0));

        assertThat(requestWithItemInfoDto.stream()
                .filter(i -> Objects.equals(i.getId(), request1.getId()))
                .findFirst().orElseThrow()
                .getItems().stream()
                .map(ItemForRequestDto::getId)
                .collect(Collectors.toList()), contains(item1.getId()));
        assertThat(requestWithItemInfoDto.stream()
                .filter(i -> Objects.equals(i.getId(), request2.getId()))
                .findFirst().orElseThrow()
                .getItems().stream()
                .map(ItemForRequestDto::getId)
                .collect(Collectors.toList()), contains(List.of(item2.getId(), item3.getId()).toArray()));
    }
}
