package com.cypr.modules.developer.service;

import com.cypr.modules.developer.entity.IdempotencyRecord;
import com.cypr.modules.developer.repository.IdempotencyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public IdempotencyService(IdempotencyRecordRepository idempotencyRecordRepository) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    public String computeHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }

    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> checkIdempotency(String idempotencyKey, String payloadJson) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        Optional<IdempotencyRecord> existingOpt = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
        if (existingOpt.isPresent()) {
            IdempotencyRecord existing = existingOpt.get();
            String currentHash = computeHash(payloadJson);

            // A3 Conflict Check
            if (!existing.getRequestHash().equals(currentHash)) {
                throw new IdempotencyKeyConflictException("Idempotency key '" + idempotencyKey + "' was used with a different request payload.");
            }

            return Optional.of(existing);
        }

        return Optional.empty();
    }

    @Transactional
    public void saveIdempotencyRecord(String idempotencyKey, String payloadJson, String responseBody, int statusCode) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) return;

        String hash = computeHash(payloadJson);
        IdempotencyRecord record = new IdempotencyRecord(idempotencyKey, hash, responseBody, statusCode);
        idempotencyRecordRepository.save(record);
    }

    public static class IdempotencyKeyConflictException extends RuntimeException {
        public IdempotencyKeyConflictException(String message) {
            super(message);
        }
    }
}
