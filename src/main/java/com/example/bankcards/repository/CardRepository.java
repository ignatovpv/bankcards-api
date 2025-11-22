package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @EntityGraph(attributePaths = "owner")
    Page<Card> findByOwnerUsername(String username, Pageable pageable);

    @EntityGraph(attributePaths = "owner")
    Optional<Card> findByIdAndOwnerUsername(Long id, String username);

    @EntityGraph(attributePaths = "owner")
    Page<Card> findByOwnerUsernameAndStatus(String username, CardStatus status, Pageable pageable);
}
