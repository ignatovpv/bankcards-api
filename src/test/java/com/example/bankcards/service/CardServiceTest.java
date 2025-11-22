package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService service;

    private static Card card(long id, CardStatus status) {
        Card c = new Card();
        c.setId(id);
        c.setStatus(status);
        c.setExpiry(LocalDate.now().plusDays(1));
        return c;
    }

    @Test
    void requestBlock_setsBlockRequested_whenActiveAndOwned() {
        Card card = card(1L, CardStatus.ACTIVE);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user"))
                .thenReturn(Optional.of(card));

        service.requestBlock(1L, "user");

        assertEquals(CardStatus.BLOCK_REQUESTED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestBlock_throwsConflict_whenStatusNotActive() {
        Card card = card(1L, CardStatus.CREATED);

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
}