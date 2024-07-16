package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(ItemMapperImpl.class)
public class ItemControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;

    private static ItemMapper itemMapper;
    private Item item;
    private User user;

    @BeforeAll
    static void beforeAll() {
        itemMapper = new ItemMapperImpl();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        item = new Item();
        item.setId(2L);
        item.setName("Item");
        item.setDescription("Item desc");
        item.setAvailable(true);
        item.setOwnerId(999L);
    }

    @Test
    void createItemWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        when(itemService.add(any(), any())).thenReturn(itemMapper.toItemDto(item));

        assertThat(getResponseCreateItem(userId, "Name", "Desc", true).getStatus(), is(200));
        verify(itemService, times(1)).add(any(), any());

        assertThat(getResponseCreateItem(userId, null, "Desc", true).getStatus(), is(400));
        assertThat(getResponseCreateItem(userId, "Name", null, true).getStatus(), is(400));
        assertThat(getResponseCreateItem(userId, "Name", "Desc", null).getStatus(), is(400));
    }

    @Test
    void getItemWithValidParametersReturnsAnswer() throws Exception {
        final long userId = user.getId();
        final long itemId = item.getId();
        Map<Long, List<Booking>> bookingMapping = new HashMap<>();
        bookingMapping.put(itemId, List.of(new Booking(1L, item,
                BookingStatus.APPROVED, user, LocalDateTime.now(), LocalDateTime.now().plusDays(1))));
        doReturn(itemMapper
                .toExtendInfoDto(List.of(item), new HashMap<>(), bookingMapping, bookingMapping).get(0))
                .when(itemService).getById(any(), any());

        when(bookingService.getItemLastBookingMapping(any())).thenReturn(bookingMapping);
        when(bookingService.getItemNextBookingMapping(any())).thenReturn(bookingMapping);

        MockHttpServletResponse response = getResponseGetItem(userId, itemId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(itemId)));
        verify(itemService, times(1)).getById(any(), any());

        // by owner
        response = getResponseGetItem(item.getOwnerId(), itemId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(itemId)));

        verify(itemService, times(2)).getById(any(), any());
    }

    @Test
    void getAllUserItemsWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        when(itemService.getAllByOwner(any(), anyInt(), anyInt())).thenReturn(itemMapper
                .toExtendInfoDto(List.of(item), new HashMap<>(), new HashMap<>(),
                        new HashMap<>()));

        assertThat(getResponseGetAllUserItems(userId, 0, 11).getStatus(), is(200));
        verify(itemService, times(1)).getAllByOwner(userId, 0, 11);

        assertThat(getResponseGetAllUserItems(userId).getStatus(), is(200));
        verify(itemService, times(1)).getAllByOwner(userId, 0, 10);

        assertThat(getResponseGetAllUserItems(userId, 0, -1).getStatus(), is(400));
        assertThat(getResponseGetAllUserItems(userId, -1, 11).getStatus(), is(400));
    }

    @Test
    void updateItemWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        final long itemId = item.getId();
        when(itemService.update(any(), any(), any())).thenReturn(itemMapper.toItemDto(item));

        ItemDto itemDto = ItemDto.builder().name("Name").description("Desc").available(true).build();
        assertThat(getResponseUpdateItem(userId, itemId, itemDto).getStatus(), is(200));
        verify(itemService, times(1)).update(itemDto, itemId, userId);

        itemDto = ItemDto.builder().name(null).description(null).available(false).build();
        assertThat(getResponseUpdateItem(userId, itemId, itemDto).getStatus(), is(200));
        verify(itemService, times(2)).update(itemDto, itemId, userId);

        assertThat(getResponseUpdateItem(userId, itemId, null).getStatus(), is(400));
    }

    @Test
    void searchAvailableItemsWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        when(itemService.searchItems(any(), anyInt(), anyInt())).thenReturn(itemMapper.toListItemDto(List.of(item)));

        assertThat(getResponseSearchItems("text", 0).getStatus(), is(200));
        verify(itemService, times(1)).searchItems("text", 0, 11);

        assertThat(getResponseSearchItems("t").getStatus(), is(200));
        verify(itemService, times(1)).searchItems("t", 0, 10);

        assertThat(getResponseSearchItems("").getStatus(), is(200));
        verify(itemService, times(0)).searchItems("", 1, 9);

        assertThat(getResponseSearchItems(null, 0).getStatus(), is(400));
    }

    @Test
    void createCommentWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        final long itemId = item.getId();

        CommentDto commentDto = CommentDto.builder().text("text").build();
        assertThat(getResponseCreateComment(userId, itemId, commentDto).getStatus(), is(200));
        verify(itemService, times(1)).createComment(commentDto, itemId, userId);

        commentDto = CommentDto.builder().text(null).build();
        assertThat(getResponseCreateComment(userId, itemId, commentDto).getStatus(), is(400));
        verify(itemService, times(1)).createComment(commentDto, itemId, userId);
    }

    private MockHttpServletResponse getResponseCreateComment(Long userId, Long itemId, CommentDto commentDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseSearchItems(String text) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items/search")
                        .param("text", text)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseSearchItems(String text, Integer from) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf((Integer) 11))
                        .param("text", text)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseUpdateItem(Long userId, Long itemId, ItemDto itemDto) throws Exception {
        MvcResult mvcResult = mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseCreateItem(Long userId, String name, String desc, Boolean available) throws Exception {
        ItemDto itemDto = ItemDto.builder().name(name).description(desc).available(available).build();
        MvcResult mvcResult = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetItem(Long userId, Long itemId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetAllUserItems(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetAllUserItems(Long userId, Integer from, Integer size) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}
