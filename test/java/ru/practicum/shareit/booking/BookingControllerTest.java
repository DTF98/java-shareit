package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.create.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = BookingController.class)
@Import({BookingMapperImpl.class, ItemMapperImpl.class, UserMapperImpl.class})
public class BookingControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private final BookingService bookingService;

    private static BookingDto booking;
    private static ItemMapper itemMapper;
    private static UserMapper userMapper;

    @BeforeAll
    static void beforeAll() {
        userMapper = new UserMapperImpl();
        itemMapper = new ItemMapperImpl();
    }

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(2L);
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = start.plusDays(2);
        booking = new BookingDto(3L, itemMapper.toItemDto(item), userMapper.toDto(user), null, start, end);
    }

    @Test
    void createBookingWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = booking.getBooker().getId();
        final long itemId = booking.getBooker().getId();
        final LocalDateTime start = booking.getStart();
        final LocalDateTime end = booking.getEnd();
        when(bookingService.createBooking(any(), any())).thenReturn(booking);

        assertThat(getResponseCreateBooking(userId, itemId, start, end).getStatus(), is(200));
        verify(bookingService, times(1)).createBooking(any(), any());

        assertThat(getResponseCreateBooking(userId, null, start, end).getStatus(), is(400));
        assertThat(getResponseCreateBooking(userId, itemId, LocalDateTime.now().minusYears(1), end).getStatus(), is(400));
        assertThat(getResponseCreateBooking(userId, itemId, start, LocalDateTime.now().minusYears(1)).getStatus(), is(400));
    }

    @Test
    void getBookingWithValidParametersReturnsAnswer() throws Exception {
        final long bookingId = booking.getId();
        final long userId = booking.getBooker().getId();

        when(bookingService.getBooking(any(), any())).thenReturn(booking);

        assertThat(getResponseGetBooking(userId, bookingId).getStatus(), is(200));
        verify(bookingService, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getBookingsByStateWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = booking.getBooker().getId();

        when(bookingService.getBookingsForUser(any(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        assertThat(getResponseGetBookingsByState(userId, 0, 11, BookingState.PAST.toString()).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForUser(userId, BookingState.PAST, 0, 11);

        assertThat(getResponseGetBookingsByState(userId, 0, 11, null).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForUser(userId, BookingState.ALL, 0, 11);

        assertThat(getResponseGetBookingsByState(userId, null).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForUser(userId, BookingState.ALL, 0, 10);

        assertThat(getResponseGetBookingsByState(userId, -1, 11, BookingState.CURRENT.toString()).getStatus(), is(400));
        assertThat(getResponseGetBookingsByState(userId, 0, -1, BookingState.PAST.toString()).getStatus(), is(400));
    }

    @Test
    void getBookingsByStateOwnerWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = booking.getBooker().getId();

        when(bookingService.getBookingsForItemOwner(any(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        assertThat(getResponseGetBookingsByStateOwner(userId, 0, 11, BookingState.PAST.toString()).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForItemOwner(userId, BookingState.PAST, 0, 11);

        assertThat(getResponseGetBookingsByStateOwner(userId, 0, 11, null).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForItemOwner(userId, BookingState.ALL, 0, 11);

        assertThat(getResponseGetBookingsByStateOwner(userId, null).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForItemOwner(userId, BookingState.ALL, 0, 10);

        assertThat(getResponseGetBookingsByStateOwner(userId, -1, 11, BookingState.CURRENT.toString()).getStatus(), is(400));
        assertThat(getResponseGetBookingsByStateOwner(userId, 0, -1, BookingState.PAST.toString()).getStatus(), is(400));
    }

    @Test
    void updateBookingStatusWithValidParametersReturnsAnswer() throws Exception {
        final long userId = booking.getBooker().getId();
        final long bookingId = booking.getId();

        when(bookingService.updateBookingStatus(any(), any(), anyBoolean())).thenReturn(booking);

        assertThat(getResponseUpdateBookingStatus(userId, bookingId, true).getStatus(), is(200));
        verify(bookingService, times(1)).updateBookingStatus(userId, bookingId, true);

        assertThat(getResponseUpdateBookingStatus(userId, bookingId, false).getStatus(), is(200));
        verify(bookingService, times(1)).updateBookingStatus(userId, bookingId, false);
    }

    private MockHttpServletResponse getResponseUpdateBookingStatus(Long userId, Long bookingId, Boolean approved) throws Exception {
        MvcResult mvcResult = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetBookingsByStateOwner(Long userId, String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetBookingsByStateOwner(Long userId, Integer from, Integer size,
                                                                   String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetBookingsByState(Long userId, String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetBookingsByState(Long userId, Integer from, Integer size,
                                                              String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetBooking(Long userId, Long bookingId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseCreateBooking(Long userId, Long itemId, LocalDateTime start, LocalDateTime end) throws Exception {
        CreateBookingDto createBookingDto = CreateBookingDto.builder().itemId(itemId).start(start).end(end).build();
        MvcResult mvcResult = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}
