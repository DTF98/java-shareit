package ru.practicum.shareit.utils;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Pageable;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaginationTest {
    @ParameterizedTest
    @MethodSource("argumentsForPagination")
    void getPageWithValidParametersReturnsAnswer(int from, int size, int expectedPage) {
        Pageable page = Pagination.getPage(from, size);
        assertThat(page.getPageNumber(), is(expectedPage));
    }

    private Stream<Arguments> argumentsForPagination() {
        return Stream.of(
                Arguments.of(4, 2, 2),
                Arguments.of(3, 2, 1),
                Arguments.of(5, 2, 2)
        );
    }
}
