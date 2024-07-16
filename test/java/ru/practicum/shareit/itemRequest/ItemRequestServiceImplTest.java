package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.AdvancedRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestService requestService;
    private final ItemRequestMapper itemRequestMapper;

    User userWithRequests;
    User userWithoutRequests;

    @BeforeEach
    void setUp() {
        userWithoutRequests = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        userWithRequests = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
    }

    @Test
    void shouldCreateAndGetRequestsByCorrectUsersWithValidParametersReturnsAnswer() {
        ItemRequestDto request = requestService.createRequest(
                new ItemRequestDto(null,"Request desc 1", null), userWithRequests.getId());
        ItemRequest savedRequest = requestRepository.findById(request.getId()).orElseThrow();
        assertEquals(itemRequestMapper.toDto(savedRequest), request);
        assertEquals(itemRequestMapper.toAdvancedItemRequestDto(savedRequest, null),
                requestService.getRequestById(userWithRequests.getId(), request.getId()));

        List<AdvancedRequestItemDto> ownRequests = requestService.getOwnRequests(userWithRequests.getId(), 0, 10);
        assertThat(ownRequests, contains(itemRequestMapper.toAdvancedItemRequestDto(savedRequest, List.of())));

        List<AdvancedRequestItemDto> ownRequestsEmpty = requestService.getOwnRequests(userWithoutRequests.getId(), 0, 10);
        assertThat(ownRequestsEmpty, hasSize(0));

        List<AdvancedRequestItemDto> allRequests = requestService.getAllRequests(userWithoutRequests.getId(), 0, 10);
        assertThat(allRequests, contains(itemRequestMapper.toAdvancedItemRequestDto(savedRequest, List.of())));
    }
}
