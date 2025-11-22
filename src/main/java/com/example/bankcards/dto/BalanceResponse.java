package com.example.bankcards.dto;

import java.math.BigDecimal;

public record BalanceResponse(BigDecimal balance) {
    public static BalanceResponse toDto(BigDecimal balance) {
        return new BalanceResponse(balance);
    }
}
