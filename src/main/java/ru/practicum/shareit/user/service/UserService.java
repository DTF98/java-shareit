package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    void delete(Long id);
}
