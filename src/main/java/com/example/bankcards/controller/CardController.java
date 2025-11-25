package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService service;

    @GetMapping
    public ResponseEntity<List<CardResponse>> getUserCards(
            @AuthenticationPrincipal UserDetails me,
            @RequestParam(required = false) CardStatus status,
            @ParameterObject Pageable pageable) {
        Page<Card> page = service.getUserCards(me.getUsername(), status, pageable);
        List<CardResponse> responseList = page.map(CardResponse::toDto).getContent();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("{id}")
    public ResponseEntity<CardResponse> getUserCard(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails me) {
        Card card = service.getUserCard(id, me.getUsername());
        CardResponse response = CardResponse.toDto(card);
        return ResponseEntity.ok(response);
    }

    @ApiResponse(responseCode = "204")
    @PostMapping("{id}/block-request")
    public ResponseEntity<Void> requestBlock(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails me) {
        service.requestBlock(id, me.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails me) {
        Card card = service.getUserCard(id, me.getUsername());
        BalanceResponse response = BalanceResponse.toDto(card.getBalance());
        return ResponseEntity.ok(response);
    }
}
