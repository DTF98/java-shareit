package ru.practicum.shareit.errorHandler.errorResponse;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String description;

    public ErrorResponse(String description) {
        this.description = description;
    }
}
