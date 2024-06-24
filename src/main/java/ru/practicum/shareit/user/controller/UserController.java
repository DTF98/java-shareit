package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationGroup;

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

    @Validated({ValidationGroup.Create.class})
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        log.info("Добавление пользователя: {}", userDto);
        return ResponseEntity.ok(userService.add(userDto));
    }

    @Validated({ValidationGroup.Update.class})
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Обновление пользователя по id = {}", userId);
        return ResponseEntity.ok(userService.update(userDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        log.info("Удаление пользователя по id = {}", id);
        userService.delete(id);
        return ResponseEntity.ok(id);
    }
}
