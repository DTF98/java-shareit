package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Pagination {
    public Pageable getPage(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public Pageable getPage(int from, int size, Sort sort) {
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}
