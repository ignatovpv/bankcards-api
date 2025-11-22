package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Converter
@Component
@RequiredArgsConstructor
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final CryptoUtil crypto;

    @Override
    public String convertToDatabaseColumn(String attr) {
        return attr == null ? null : crypto.encrypt(attr);
    }

    @Override
    public String convertToEntityAttribute(String db) {
        return db == null ? null : crypto.decrypt(db);
    }
}