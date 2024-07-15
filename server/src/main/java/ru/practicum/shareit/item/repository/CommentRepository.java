package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
     Comment findByAuthorIdAndItemId(Long authorId, Long itemId);

     List<Comment> findAllByItemIdIn(Set<Long> itemIds);
}
