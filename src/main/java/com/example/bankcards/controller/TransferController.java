package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request,
                                                     @AuthenticationPrincipal UserDetails me
    ) {
        Transfer saved = service.transfer(
                request.fromCardId(),
                request.toCardId(),
                request.amount(),
                me.getUsername()
        );

        TransferResponse response = TransferResponse.fromEntity(saved);
        return ResponseEntity.ok(response);
    }
}
