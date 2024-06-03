package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public List<UserDto> getAll() {
        return UserMapper.toDto(userStorage.getAll());
    }

    public UserDto getById(Long id) {
        return UserMapper.toDto(userStorage.getById(id).orElseThrow(() ->
                new NotFoundException(String.format("Не найден пользователь по id = %s", id))));
    }

    public UserDto add(User user) {
        if (userStorage.isExistEmail(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
        User userAdd = userStorage.add(user);
        return UserMapper.toDto(userAdd);
    }

    public UserDto update(UserDto userDto, Long userId) {
        UserDto userUpdate = checkUserForUpdate(userDto, userId);
        return UserMapper.toDto(userStorage.update(userUpdate));
    }

    public UserDto delete(Long id) {
        getById(id);
        return UserMapper.toDto(userStorage.delete(id));
    }

    private UserDto checkUserForUpdate(UserDto userDto, Long userId) {
        getById(userId);
        if (userStorage.isExistEmail(userDto.getEmail())) {
            if (!userStorage.getEmailById(userId).equals(userDto.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует!");
            }
        }
        userDto.setId(userId);
        return userDto;
    }
}
