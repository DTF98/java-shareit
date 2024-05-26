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
    private final UserStorage storage;

    public List<UserDto> getAll() {
        return UserMapper.toDto(storage.getAll());
    }

    public UserDto getById(Long id) {
        return UserMapper.toDto(storage.getById(id).orElseThrow(() ->
                new NotFoundException(String.format("Не найден пользователь по id = %s", id))));
    }

    public UserDto add(User user) {
        if (storage.isExistEmail(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
        User userAdd = storage.add(user);
        return UserMapper.toDto(userAdd);
    }

    public UserDto update(UserDto userDto, Long userId) {
        if (storage.isExistEmail(userDto.getEmail())) {
            if (!storage.getEmailById(userId).equals(userDto.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует!");
            }
        }
        getById(userId);
        userDto.setId(userId);
        return UserMapper.toDto(storage.update(userDto));
    }

    public UserDto delete(Long id) {
        getById(id);
        return UserMapper.toDto(storage.delete(id));
    }
}
