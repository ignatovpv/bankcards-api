package com.example.bankcards.service;

import com.example.bankcards.entity.UserAccount;
import com.example.bankcards.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserAccountRepository userRepository;

    @Transactional
    public Card create(Long userId, String pan, LocalDate expiry) {
        UserAccount owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String numberHash = hashPan(pan);

        Card card = Card.builder()
                .number(pan)
                .numberHash(numberHash)
                .owner(owner)
                .expiry(expiry)
                .balance(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY))
                .status(CardStatus.CREATED)
                .build();

        try {
            return cardRepository.save(card);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Card with this number already exists", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Card> list(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Transactional
    public void requestBlock(Long id, String owner) {
        Card card = cardRepository.findByIdAndOwnerUsername(id, owner)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        validateNotExpired(card);

        if (card.getStatus() == CardStatus.BLOCK_REQUESTED ||
                card.getStatus() == CardStatus.BLOCKED) {
            return;
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new ConflictException("Only active cards can be blocked");
        }

        card.setStatus(CardStatus.BLOCK_REQUESTED);
        cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public Card getUserCard(Long id, String username) {
        return cardRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    @Transactional(readOnly = true)
    public Page<Card> getUserCards(String owner, CardStatus status, Pageable pageable) {
        if (status == null) {
            return cardRepository.findByOwnerUsername(owner, pageable);
        }
        return cardRepository.findByOwnerUsernameAndStatus(owner, status, pageable);
    }

    private void validateNotExpired(Card card) {
        if (card.getExpiry().isBefore(LocalDate.now())) {
            throw new ConflictException("Card is expired");
        }
    }

    private String hashPan(String pan) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(pan.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
