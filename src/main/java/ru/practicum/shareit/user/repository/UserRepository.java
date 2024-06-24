package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    <T> Optional<T> findById(Long userId, Class<T> type);

    <T> List<T> findBy(Class<T> type);
}