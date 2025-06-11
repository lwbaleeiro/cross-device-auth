package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.User;

import java.util.UUID;

public interface LoginRequestService {
    LoginRequest create(String deviceIdRequester, String deviceNameRequester);
    LoginRequest getLoginStatus(UUID id);
    LoginRequest loginApprove(UUID id, String deviceIdApprove, User user);
    LoginRequest loginReject(UUID id, String deviceIdReject, User user);
}
