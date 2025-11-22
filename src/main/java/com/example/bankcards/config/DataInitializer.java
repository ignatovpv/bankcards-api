package com.example.bankcards.config;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserAccount;
import com.example.bankcards.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userRepository;

    @Bean
    CommandLineRunner loadData() {
        return args -> loadUsers();
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

        log.info("----Test users loaded----");
    }
}