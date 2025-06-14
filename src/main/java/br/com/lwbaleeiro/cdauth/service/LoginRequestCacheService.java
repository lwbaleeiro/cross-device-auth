package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestCache;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;

import java.util.Optional;
import java.util.UUID;

public interface LoginRequestCacheService {
    void cacheLoginRequest(UUID loginRequestId, LoginRequest loginRequest);
    Optional<LoginRequestCache> getLoginRequest(UUID loginRequestId);
    //Optional<LoginRequest> updateLoginRequest(UUID loginRequestId, String newStatus, String userId);
    void deleteLoginRequest(UUID loginRequestId);
}
