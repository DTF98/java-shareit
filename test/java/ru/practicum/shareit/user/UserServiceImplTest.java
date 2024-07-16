package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserMapperImpl.class)
public class UserServiceImplTest {

    private final UserRepository userRepository;
    private final UserService userService;
    private static UserMapper userMapper;

    @BeforeAll
    static void beforeAll() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void shouldSaveAndUpdateAndGetAndDeleteUserWithValidParametersReturnsAnswerAndInvalidParametersReturnsError() {
        UserDto newUser = userService.add(userMapper.toDto(new User(null, "User 1", "user1@mail.com")));
        User savedUser = userRepository.findById(newUser.getId()).orElseThrow();
        assertEquals(newUser, userMapper.toDto(savedUser));
        assertEquals(userMapper.toDto(savedUser), userService.getById(newUser.getId()));
        assertThat(userService.getAll(), contains(userMapper.toDto(savedUser)));

        User noExistUser = new User(999L, "User 1", "user1@mail.com");
        final User userUpdater = new User(null, "User updated name", null);
        assertThrows(NotFoundException.class, () -> userService.update(userMapper.toDto(userUpdater), noExistUser.getId()));
        userService.update(userMapper.toDto(userUpdater), newUser.getId());
        assertEquals(savedUser.getName(), "User updated name");
        assertEquals(savedUser.getEmail(), "user1@mail.com");

        userService.delete(newUser.getId());
        assertThrows(NotFoundException.class, () -> userService.getById(newUser.getId()));
    }
}
