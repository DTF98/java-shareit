package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class UserEmailStorage {
    private final Map<Long,String> storage = new HashMap<>();

    public void addEmail(Long id,String email) {
        storage.put(id, email);
    }

    public boolean contains(String email) {
        return storage.containsValue(email);
    }

    public String getEmailById(Long id) {
        return storage.get(id);
    }

    public void deleteEmail(Long id) {
        storage.remove(id);
    }
}
