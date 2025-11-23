package com.example.bankcards.service;

import lombok.RequiredArgsConstructor;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final CardService cardService;
    private final TransferRepository transferRepository;

    @Transactional
    public Transfer transfer(Long fromId, Long toId, BigDecimal amount, String owner) {

        if (amount == null || amount.signum() <= 0) {
            throw new ConflictException("Amount must be positive");
        }

        Long firstId  = fromId < toId ? fromId : toId;
        Long secondId = fromId < toId ? toId   : fromId;

        Card first = cardService.getActiveUserCard(firstId, owner);
        Card second = cardService.getActiveUserCard(secondId, owner);

        Card from = first.getId().equals(fromId) ? first : second;
        Card to   = first.getId().equals(toId)   ? first : second;

        if (from.getBalance().compareTo(amount) < 0) {
            throw new ConflictException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        return transferRepository.save(Transfer.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .build());
    }
}
