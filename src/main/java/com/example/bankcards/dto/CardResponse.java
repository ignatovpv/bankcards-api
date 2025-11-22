package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardMaskUtil;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(Long id,
                           String maskedNumber,
                           String owner,
                           LocalDate expiry,
                           BigDecimal balance,
                           String status
) {
    public static CardResponse toDto(Card card) {
        return new CardResponse(
                card.getId(),
                CardMaskUtil.maskCard(card.getNumber()),
                card.getOwner().getUsername(),
                card.getExpiry(),
                card.getBalance(),
                card.getStatus().name()
        );
    }
}