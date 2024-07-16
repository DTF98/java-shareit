package ru.practicum.shareit.itemRequest;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(ItemRequestMapperImpl.class)
public class ItemRequestControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemService itemService;

    private static ItemRequestMapper itemRequestMapper;
    private ItemRequest request;
    private User user;

    @BeforeAll
    static void beforeAll() {
        itemRequestMapper = new ItemRequestMapperImpl();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        request = new ItemRequest();
        request.setId(3L);
    }

    @Test
    void createRequestWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        when(itemRequestService.createRequest(any(), any())).thenReturn(itemRequestMapper.toDto(request));

        ItemRequestDto requestDto = ItemRequestDto.builder().description("Desc").build();
        assertThat(getResponseCreateRequest(userId, requestDto).getStatus(), is(200));
        verify(itemRequestService, times(1)).createRequest(requestDto, userId);

        requestDto = ItemRequestDto.builder().description(null).build();
        assertThat(getResponseCreateRequest(userId, requestDto).getStatus(), is(400));
        verify(itemRequestService, times(1)).createRequest(requestDto, userId);
    }

    @Test
    void getOwnRequestsWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        Map<Long, List<Item>> requestItemMapping = new HashMap<>();
        requestItemMapping.put(request.getId(), new ArrayList<>());
        when(itemRequestService.getOwnRequests(any(), anyInt(), anyInt())).thenReturn(itemRequestMapper
                .toListAdvancedItemRequestDto(List.of(request), requestItemMapping));
        when(itemRequestService.getRequestItemMapping(anySet())).thenReturn(requestItemMapping);

        assertThat(getResponseGetOwnRequestsGetOwnRequests(userId, 0, 11).getStatus(), is(200));
        verify(itemRequestService, times(1)).getOwnRequests(userId, 0, 11);

        assertThat(getResponseGetOwnRequestsGetOwnRequests(userId).getStatus(), is(200));
        verify(itemRequestService, times(1)).getOwnRequests(userId, 0, 10);

        assertThat(getResponseGetOwnRequestsGetOwnRequests(userId, -1, 11).getStatus(), is(400));
        verify(itemRequestService, times(0)).getOwnRequests(userId, -1, 11);

        assertThat(getResponseGetOwnRequestsGetOwnRequests(userId, 0, -1).getStatus(), is(400));
        verify(itemRequestService, times(0)).getOwnRequests(userId, 0, -1);
    }

    @Test
    void getAllRequestsWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        Map<Long, List<Item>> requestItemMapping = new HashMap<>();
        requestItemMapping.put(request.getId(), new ArrayList<>());
        when(itemRequestService.getAllRequests(any(), anyInt(), anyInt())).thenReturn(itemRequestMapper
                .toListAdvancedItemRequestDto(List.of(request), requestItemMapping));
        when(itemRequestService.getRequestItemMapping(anySet())).thenReturn(requestItemMapping);

        assertThat(getResponseGetAllRequests(userId, 0, 11).getStatus(), is(200));
        verify(itemRequestService, times(1)).getAllRequests(userId, 0, 11);

        assertThat(getResponseGetAllRequests(userId).getStatus(), is(200));
        verify(itemRequestService, times(1)).getAllRequests(userId, 0, 10);

        assertThat(getResponseGetAllRequests(userId, -1, 11).getStatus(), is(400));
        verify(itemRequestService, times(0)).getAllRequests(userId, -1, 11);

        assertThat(getResponseGetAllRequests(userId, 0, -1).getStatus(), is(400));
        verify(itemRequestService, times(0)).getAllRequests(userId, 0, -1);
    }

    @Test
    void getRequestByIdWithValidParametersReturnsAnswer() throws Exception {
        final long userId = user.getId();
        final long requestId = request.getId();
        Map<Long, List<Item>> requestItemMapping = new HashMap<>();
        requestItemMapping.put(request.getId(), new ArrayList<>());
        when(itemRequestService.getRequestById(any(), any())).thenReturn(itemRequestMapper.toAdvancedItemRequestDto(request, null));
        when(itemRequestService.getRequestItemMapping(anySet())).thenReturn(requestItemMapping);

        MockHttpServletResponse response = getResponseGetRequestById(userId, requestId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(requestId)));
        verify(itemRequestService, times(1)).getRequestById(userId, requestId);
    }

    private MockHttpServletResponse getResponseCreateRequest(Long userId, ItemRequestDto requestDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetOwnRequestsGetOwnRequests(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetOwnRequestsGetOwnRequests(Long userId, Integer from, Integer size)
            throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetAllRequests(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetAllRequests(Long userId, Integer from, Integer size)
            throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetRequestById(Long userId, Long requestId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}
