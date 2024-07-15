package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long id, Pageable page);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String searchNameBy,
                                                                                               String searchDescBy,
                                                                                               Boolean available,
                                                                                               Pageable page);

    List<Item> findAllByRequestIdIn(Set<Long> requestIds);
}
