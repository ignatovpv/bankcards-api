package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import com.example.bankcards.entity.Card;

import java.time.LocalDate;

public record CardUpdateRequest(
    @Future(message = "Expiry date must be in the future")
    LocalDate expiry
) {
    public Card toEntity() {
        return Card.builder()
                .expiry(expiry)
                .build();
    }
}