package com.example.bankcards.dto;

import com.example.bankcards.entity.Transfer;
import java.math.BigDecimal;

public record TransferResponse(
        Long id,
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
    public static TransferResponse fromEntity(Transfer t) {
        return new TransferResponse(
                t.getId(),
                t.getFromCard().getId(),
                t.getToCard().getId(),
                t.getAmount()
        );
    }
}