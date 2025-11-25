package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public record TransferRequest(
        @NotNull Long fromCardId,
        @NotNull Long toCardId,
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive") BigDecimal amount
) {
    @Schema(hidden = true)
    @AssertTrue(message = "Source and destination cards must be different")
    public boolean isDifferentCards() {
        return !Objects.equals(fromCardId, toCardId);
    }
}
