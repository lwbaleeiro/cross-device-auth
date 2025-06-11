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
    private final DeviceService deviceService;

    @Autowired
    public LoginRequestServiceImpl(LoginRequestRepository loginRequestRepository, DeviceService deviceService) {
        this.loginRequestRepository = loginRequestRepository;
        this.deviceService = deviceService;
    }

    @Override
    public LoginRequest create(String deviceIdRequester, String deviceNameRequester) {

        LoginRequest loginRequest = LoginRequest
                .builder()
                .deviceIdRequester(deviceIdRequester)
                .deviceNameRequester(deviceNameRequester)
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

        /* TODO: Implementar depois
        * Setar quando vai expirar ? loginRequest.setExpiresAt();
        * Adicionar validações como:
        * - Se um LoginRequest já foi aprovado/reprovado nõo poderá ser atualizado novamente.
        * - Precisa gerar um novo LoginRequest
        * */

       deviceService.create(loginRequest.getDeviceIdRequester(),
               loginRequest.getDeviceNameRequester(),
               user);

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
