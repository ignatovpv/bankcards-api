package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserAccount;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    UserAccountRepository userAccountRepository;

    @InjectMocks
    private CardService service;

    private static Card card(CardStatus status) {
        Card c = new Card();
        c.setId(1L);
        c.setStatus(status);
        c.setExpiry(LocalDate.now().plusDays(1));
        return c;
    }

    @Test
    void requestBlock_setsBlockRequested_whenActiveAndOwned() {
        Card card = card(CardStatus.ACTIVE);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user"))
                .thenReturn(Optional.of(card));

        service.requestBlock(1L, "user");

        assertEquals(CardStatus.BLOCK_REQUESTED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestBlock_throwsConflict_whenStatusNotActive() {
        Card card = card(CardStatus.CREATED);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user"))
                .thenReturn(Optional.of(card));

        assertThrows(ConflictException.class,
                () -> service.requestBlock(1L, "user"));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void getUserCards_usesOwnerOnly_whenStatusNull() {
        Pageable pageable = Pageable.unpaged();
        Page<Card> page = Page.empty(pageable);

        when(cardRepository.findByOwnerUsername("user", pageable))
                .thenReturn(page);

        Page<Card> result = service.getUserCards("user", null, pageable);

        assertSame(page, result);
        verify(cardRepository).findByOwnerUsername("user", pageable);
        verify(cardRepository, never())
                .findByOwnerUsernameAndStatus(anyString(), any(), any());
    }

    @Test
    void getUserCards_usesOwnerAndStatus_whenStatusProvided() {
        Pageable pageable = Pageable.unpaged();
        Page<Card> page = Page.empty(pageable);

        when(cardRepository.findByOwnerUsernameAndStatus("user", CardStatus.ACTIVE, pageable))
                .thenReturn(page);

        Page<Card> result = service.getUserCards("user", CardStatus.ACTIVE, pageable);

        assertSame(page, result);
        verify(cardRepository)
                .findByOwnerUsernameAndStatus("user", CardStatus.ACTIVE, pageable);
        verify(cardRepository, never())
                .findByOwnerUsername(anyString(), any());
    }

    @Test
    void create_buildsCreatedCard_whenUserExists() {
        UserAccount owner = new UserAccount();
        owner.setId(10L);
        owner.setUsername("user");

        when(userAccountRepository.findById(10L))
                .thenReturn(Optional.of(owner));
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDate expiry = LocalDate.now().plusYears(2);

        Card result = service.create(10L, "1111222233334444", expiry);

        assertEquals(owner, result.getOwner());
        assertEquals(expiry, result.getExpiry());
        assertEquals(CardStatus.CREATED, result.getStatus());
        assertEquals(new BigDecimal("0.00"), result.getBalance());
        assertNotNull(result.getNumberHash());
        verify(cardRepository).save(result);
    }

    @Test
    void activate_setsActive_whenCreatedAndNotExpired() {
        Card card = card(CardStatus.CREATED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        service.activate(1L);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void approveBlock_setsBlocked_whenBlockRequestedAndNotExpired() {
        Card card = card(CardStatus.BLOCK_REQUESTED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        service.approveBlock(1L);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }
}