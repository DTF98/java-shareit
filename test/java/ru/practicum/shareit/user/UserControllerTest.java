package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserMapperImpl.class)
public class UserControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private static UserMapper userMapper;
    private User user;

    @BeforeAll
    static void beforeAll() {
        userMapper = new UserMapperImpl();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("user@mail.com");
    }

    @Test
    void createWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        when(userService.add(any())).thenReturn(userMapper.toDto(user));

        UserDto userDto = UserDto.builder().name(user.getName()).email(user.getEmail()).build();
        assertThat(getResponseCreate(userDto).getStatus(), is(200));

        userDto = UserDto.builder().name(null).email(user.getEmail()).build();
        assertThat(getResponseCreate(userDto).getStatus(), is(400));

        userDto = UserDto.builder().name(user.getName()).email(null).build();
        assertThat(getResponseCreate(userDto).getStatus(), is(400));

        userDto = UserDto.builder().name(user.getName()).email("mail").build();
        assertThat(getResponseCreate(userDto).getStatus(), is(400));

        userDto = UserDto.builder().name(user.getName()).email("mail@mail").build();
        assertThat(getResponseCreate(userDto).getStatus(), is(400));

        verify(userService, times(1)).add(any());
    }

    @Test
    void getAllWithValidParametersReturnsAnswer() throws Exception {
        final long userId = user.getId();
        when(userService.getAll()).thenReturn(List.of(userMapper.toDto(user)));

        MockHttpServletResponse response = getResponseGetAll();

        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].id").toString(), is(String.valueOf(userId)));
        verify(userService, times(1)).getAll();
    }

    @Test
    void updateWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() throws Exception {
        final long userId = user.getId();
        when(userService.update(any(), any())).thenReturn(userMapper.toDto(user));

        UserDto userDto = UserDto.builder().name("updated name").email("updated_email@mail.com").build();
        assertThat(getResponseUpdate(userId, userDto).getStatus(), is(200));
        verify(userService, times(1)).update(any(), any());

        userDto = UserDto.builder().name("updated name").email("updated wrong email").build();
        assertThat(getResponseUpdate(userId, userDto).getStatus(), is(400));

        userDto = UserDto.builder().name("n").build();
        assertThat(getResponseUpdate(userId, userDto).getStatus(), is(400));

        userDto = UserDto.builder().name("").build();
        assertThat(getResponseUpdate(userId, userDto).getStatus(), is(400));

        userDto = UserDto.builder().email("").build();
        assertThat(getResponseUpdate(userId, userDto).getStatus(), is(400));
    }

    @Test
    void deleteUserWithValidParametersReturnsAnswer() throws Exception {
        final long userId = user.getId();

        assertThat(getResponseDeleteUser(userId).getStatus(), is(200));
        verify(userService, times(1)).delete(any());
    }

    private MockHttpServletResponse getResponseCreate(UserDto userDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseGetAll() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseUpdate(Long userId, UserDto userDto) throws Exception {
        MvcResult mvcResult = mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse getResponseDeleteUser(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}
