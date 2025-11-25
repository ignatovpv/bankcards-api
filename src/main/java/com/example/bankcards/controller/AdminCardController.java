package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final CardService service;

    @ApiResponse(responseCode = "201")
    @PostMapping
    public ResponseEntity<CardResponse> create(@RequestBody @Valid CardCreateRequest request) {
        Card saved = service.create(
                request.userId(),
                request.number(),
                request.expiry()
        );
        CardResponse response = CardResponse.toDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardResponse> update(@PathVariable Long id,
                                               @RequestBody @Valid CardUpdateRequest request) {
        Card changes = request.toEntity();
        Card updated = service.update(id, changes);
        CardResponse response = CardResponse.toDto(updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> get(@PathVariable Long id) {
        Card card = service.getById(id);
        CardResponse response = CardResponse.toDto(card);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> list(@ParameterObject Pageable pageable) {
        Page<Card> page = service.list(pageable);
        List<CardResponse> list = page.map(CardResponse::toDto)
                .getContent();
        return ResponseEntity.ok(list);
    }

    @ApiResponse(responseCode = "204")
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204")
    @PostMapping("/{id}/block-approve")
    public ResponseEntity<Void> approveBlock(@PathVariable Long id) {
        service.approveBlock(id);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
