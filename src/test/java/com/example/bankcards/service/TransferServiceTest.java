package com.example.bankcards.service;

import com.example.bankcards.entity.*;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock CardService cardService;
    @Mock TransferRepository transferRepository;

    @InjectMocks TransferService service;

    private static Card card(Long id, String owner, BigDecimal balance, CardStatus status) {
        UserAccount user = UserAccount.builder()
                .username(owner)
                .build();

        return Card.builder()
                .id(id)
                .owner(user)
                .balance(balance)
                .status(status)
                .build();
    }

    @Test
    void should_transfer_successfully() {
        var from = card(1L, "user", new BigDecimal("100.00"), CardStatus.ACTIVE);
        var to   = card(2L, "user", new BigDecimal("50.00"),  CardStatus.ACTIVE);

        when(cardService.getActiveUserCard(1L, "user")).thenReturn(from);
        when(cardService.getActiveUserCard(2L, "user")).thenReturn(to);

        when(transferRepository.save(any(Transfer.class))).thenAnswer(inv -> {
            Transfer t = inv.getArgument(0);
            t.setId(777L);
            return t;
        });

        var saved = service.transfer(1L, 2L, new BigDecimal("30.00"), "user");

        assertEquals(new BigDecimal("70.00"), from.getBalance());
        assertEquals(new BigDecimal("80.00"), to.getBalance());
        assertEquals(777L, saved.getId());
        assertEquals(1L, saved.getFromCard().getId());
        assertEquals(2L, saved.getToCard().getId());
        assertEquals(new BigDecimal("30.00"), saved.getAmount());

        verify(transferRepository, times(1)).save(any(Transfer.class));
        verify(cardService, times(1)).getActiveUserCard(1L, "user");
        verify(cardService, times(1)).getActiveUserCard(2L, "user");
    }

    @Test
    void should_fail_when_card_not_owned_or_inactive() {
        when(cardService.getActiveUserCard(1L, "user"))
                .thenThrow(new NotFoundException("Active card not found"));

        BigDecimal amount = new BigDecimal("1.00");

        assertThrows(NotFoundException.class,
                () -> service.transfer(1L, 2L, amount, "user"));

        verifyNoInteractions(transferRepository);
        verify(cardService, times(1)).getActiveUserCard(1L, "user");
        verify(cardService, never()).getActiveUserCard(2L, "user");
    }

    @Test
    void should_fail_when_insufficient_funds() {
        var from = card(1L, "user", new BigDecimal("10"), CardStatus.ACTIVE);
        var to   = card(2L, "user", new BigDecimal("50"), CardStatus.ACTIVE);

        when(cardService.getActiveUserCard(1L, "user")).thenReturn(from);
        when(cardService.getActiveUserCard(2L, "user")).thenReturn(to);

        BigDecimal amount = new BigDecimal("100");

        assertThrows(ConflictException.class,
                () -> service.transfer(1L, 2L, amount, "user"));

        assertEquals(new BigDecimal("10"), from.getBalance());
        assertEquals(new BigDecimal("50"), to.getBalance());
        verify(transferRepository, never()).save(any());
    }
}