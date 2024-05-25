package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto add(User user);

    UserDto update(UserDto userDto, Long userId);

    UserDto delete(Long id);
}
