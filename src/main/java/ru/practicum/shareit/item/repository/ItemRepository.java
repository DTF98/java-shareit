package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    <T> Optional<T> findById(Long itemId, Class<T> type);

    <T> List<T> findAllByOwnerId(Long id, Class<T> type);

    <T> List<T> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String searchNameBy,
                                                                                               String searchDescBy,
                                                                                               Boolean available,
                                                                                               Class<T> type);
}
