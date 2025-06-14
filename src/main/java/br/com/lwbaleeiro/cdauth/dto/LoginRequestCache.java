package br.com.lwbaleeiro.cdauth.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginRequestCache(UUID id,
                                String status,
                                Instant expiresAt,
                                String userId) {
}
