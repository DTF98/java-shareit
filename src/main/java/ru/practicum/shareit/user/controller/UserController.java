package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAll() {
        log.info("Получение списка всех пользователей");
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        log.info("Получение пользователя по id = {}", id);
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody User user) {
        log.info("Добавление пользователя: {}", user);
        return ResponseEntity.ok(userService.add(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Обновление пользователя по id = {}", userId);
        return ResponseEntity.ok(userService.update(userDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> delete(@PathVariable Long id) {
        log.info("Удаление пользователя по id = {}", id);
        return ResponseEntity.ok(userService.delete(id));
    }
}
