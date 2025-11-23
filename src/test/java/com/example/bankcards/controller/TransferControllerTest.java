package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TransferService transferService;

    @Test
    @WithMockUser(username = "user")
    void should_transfer() throws Exception {
        Card from = Card.builder()
                .id(1L)
                .build();
        Card to = Card.builder()
                .id(2L)
                .build();

        Transfer transfer = Transfer.builder()
                .id(10L)
                .fromCard(from)
                .toCard(to)
                .amount(new BigDecimal("30.00"))
                .build();

        when(transferService.transfer(1L, 2L, new BigDecimal("30.00"), "user"))
                .thenReturn(transfer);

        mockMvc.perform(post("/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                   "fromCardId": 1,
                                   "toCardId": 2,
                                   "amount": 30.00
                                 }
                                 """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.amount").value(30.00));
    }
}
