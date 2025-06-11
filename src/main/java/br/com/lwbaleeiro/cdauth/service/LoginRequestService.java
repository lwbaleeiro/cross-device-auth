package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.LoginRequest;

import java.util.UUID;

public interface LoginRequestService {
    LoginRequest create(String deviceIdRequester);
    LoginRequest getLoginStatus(UUID id);
}
