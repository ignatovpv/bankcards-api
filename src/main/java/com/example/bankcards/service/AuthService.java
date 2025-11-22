package com.example.bankcards.service;

import lombok.RequiredArgsConstructor;
import com.example.bankcards.dto.auth.AuthRequest;
import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String username = auth.getName();
        String role = auth.getAuthorities().stream()
                .findFirst().orElseThrow()
                .getAuthority().replace("ROLE_", "");

        String token = jwtService.generateToken(username, role);
        return new AuthResponse(token);
    }
}
