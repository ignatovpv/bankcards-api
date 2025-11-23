package com.example.bankcards.config;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserAccount;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserAccountRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;

    @Bean
    CommandLineRunner loadData() {
        return args -> {
            loadUsers();
            loadCards();
        };
    }

    private void loadUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(UserAccount.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        userRepository.save(UserAccount.builder()
                .username("user")
                .passwordHash(passwordEncoder.encode("user123"))
                .role(Role.USER)
                .build());

        log.info("Test users loaded");
    }

    private void loadCards() {
        if (cardRepository.count() > 0) {
            return;
        }

        UserAccount user = userRepository.findByUsername("user")
                .orElseThrow(() -> new IllegalStateException("User 'user' not found"));
        UserAccount admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("User 'admin' not found"));

        Card card1 = cardService.create(user.getId(), "1111222233334444", LocalDate.now().plusYears(2));
        Card card2 = cardService.create(user.getId(), "5555666677778888", LocalDate.now().plusYears(2));
        Card card3 = cardService.create(admin.getId(), "9999000011112222", LocalDate.now().plusYears(2));

        card1.setBalance(new BigDecimal("1000.00"));
        card1.setStatus(CardStatus.ACTIVE);
        card2.setBalance(new BigDecimal("250.00"));
        card2.setStatus(CardStatus.ACTIVE);
        card3.setBalance(new BigDecimal("5000.00"));

        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);

        log.info("Test cards loaded");
    }
}