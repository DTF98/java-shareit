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
import ru.practicum.shareit.user.storage.UserEmailStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserEmailStorage emailStorage;

    public List<UserDto> getAll() {
        return storage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        try {
            return UserMapper.toUserDto(storage.getById(id));
        } catch (NullPointerException e) {
            log.error("Пользователь по id = {} не найден", id);
            throw new NotFoundException(String.format("Не найден пользователь по id = %s", id));
        }
    }

    public UserDto add(User user) {
        if (emailStorage.contains(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
        User userAdd = storage.add(user);
        emailStorage.addEmail(userAdd.getId(), userAdd.getEmail());
        return UserMapper.toUserDto(userAdd);
    }

    public UserDto update(UserDto userDto, Long userId) {
        if (emailStorage.contains(userDto.getEmail())) {
            if (!emailStorage.getEmailById(userId).equals(userDto.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует!");
            }
        }
        getById(userId);
        userDto.setId(userId);
        if (userDto.getEmail() != null) emailStorage.addEmail(userId, userDto.getEmail());
        return UserMapper.toUserDto(storage.update(userDto));
    }

    public UserDto delete(Long id) {
        getById(id);
        emailStorage.deleteEmail(id);
        return UserMapper.toUserDto(storage.delete(id));
    }
}
