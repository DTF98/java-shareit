package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserStorage {
    private final HashMap<Long, User> storage = new HashMap<>();
    private Long id = 1L;

    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<User> getById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public User add(User user) {
        user.setId(id);
        id++;
        storage.put(user.getId(), user);
        return storage.get(user.getId());
    }

    public User update(UserDto userDto) {
        User updatedUser = storage.get(userDto.getId());
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        storage.put(updatedUser.getId(), updatedUser);
        return storage.get(updatedUser.getId());
    }

    public User delete(Long id) {
        return storage.remove(id);
    }

    public boolean isExistEmail(String email) {
        return storage.values().stream()
                .anyMatch((a) -> a.getEmail().equals(email));
    }

    public String getEmailById(Long id) {
        return storage.get(id).getEmail();
    }
}
