package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.bankcards.util.EncryptedStringConverter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "cards")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "encrypted_number", nullable = false, length = 512)
    private String number;

    @Column(name = "number_hash", nullable = false, length = 64, unique = true)
    private String numberHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount owner;

    @Column(nullable = false)
    private LocalDate expiry;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CardStatus status;
}