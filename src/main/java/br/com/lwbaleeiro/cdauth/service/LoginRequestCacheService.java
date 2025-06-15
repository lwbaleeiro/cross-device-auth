package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;

import java.util.Optional;
import java.util.UUID;

public interface LoginRequestCacheService {
    void cacheLoginRequest(UUID loginRequestId, LoginRequest loginRequest);
    Optional<LoginRequestResponse> getLoginRequest(UUID loginRequestId);
    void deleteLoginRequest(UUID loginRequestId);
}
