package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findBy(UserDto.class);
    }

    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userRepository.findById(id, UserDto.class).orElseThrow(() ->
                new NotFoundException(String.format("Не найден пользователь по id = %s", id)));
    }

    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userMapper.toModel(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        User user = userMapper.toModel(getById(userId));
        return userMapper.toDto(userRepository.save(userMapper.updateModel(user, userDto)));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
