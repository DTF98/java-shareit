package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "commentDto.id")
    Comment toModel(CommentDto commentDto, User author, Long itemId);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);
}
