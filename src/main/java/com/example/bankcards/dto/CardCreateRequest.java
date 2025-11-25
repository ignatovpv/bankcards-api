package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CardCreateRequest(
        @NotNull Long userId,
        @NotBlank @Size(min = 16, max = 16) @Pattern(regexp = "\\d{16}") String number,
        @NotNull @Future LocalDate expiry
) {
}
