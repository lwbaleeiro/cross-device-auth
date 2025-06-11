package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.LoginRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoginRequestServiceImpl implements LoginRequestService {

    private final LoginRequestRepository loginRequestRepository;

    @Autowired
    public LoginRequestServiceImpl(LoginRequestRepository loginRequestRepository) {
        this.loginRequestRepository = loginRequestRepository;
    }

    @Override
    public LoginRequest create(String deviceIdRequester) {

        LoginRequest loginRequest = LoginRequest
                .builder()
                .deviceIdRequester(deviceIdRequester)
                .createdAt(Instant.now())
                .status(LoginRequestStatus.PENDING)
                .build();

        return loginRequestRepository.save(loginRequest);
    }

    @Override
    public LoginRequest getLoginStatus(UUID id) {
        return loginRequestRepository.findById(id).orElseThrow();
    }

    @Override
    public LoginRequest loginApprove(UUID id, String deviceIdApprove, User user) {

        LoginRequest loginRequest = loginRequestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Login request not found by given ID."));

        loginRequest.setDeviceIdApprove(deviceIdApprove);
        loginRequest.setUser(user);
        loginRequest.setStatus(LoginRequestStatus.APPROVED);
        //loginRequest.setExpiresAt(); //TODO: Implementar depois

        // TODO: Salvar novo dispositivo aprovado no Devices do User.

        return loginRequestRepository.save(loginRequest);
    }

    @Override
    public LoginRequest loginReject(UUID id, String deviceIdReject, User user) {

        LoginRequest loginRequest = loginRequestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Login request not found by given ID."));

        loginRequest.setDeviceIdApprove(deviceIdReject);
        loginRequest.setUser(user);
        loginRequest.setStatus(LoginRequestStatus.REJECTED);

        return loginRequestRepository.save(loginRequest);
    }
}
