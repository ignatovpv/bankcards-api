package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record CardCreateRequest(
        @NotNull Long userId,
        @NotBlank @Pattern(regexp = "\\d{16}") String number,
        @NotNull @Future LocalDate expiry
) {
}
