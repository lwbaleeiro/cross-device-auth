package br.com.lwbaleeiro.cdauth.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginRequestResponse(UUID id,
                                   String status,
                                   Instant expiresAt,
                                   UUID userId,
                                   String authToken) {
}
