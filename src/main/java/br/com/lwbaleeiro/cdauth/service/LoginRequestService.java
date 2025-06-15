package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.User;

import java.util.UUID;

public interface LoginRequestService {
    LoginRequestResponse create(String deviceIdRequester, String deviceNameRequester);
    LoginRequestResponse getLoginStatus(UUID id);
    LoginRequestResponse loginApprove(UUID id, String deviceIdApprove, User user);
    LoginRequestResponse loginReject(UUID id, String deviceIdReject, User user);
}
