package br.com.lwbaleeiro.cdauth.service.impl;

import br.com.lwbaleeiro.cdauth.controller.LoginRequestEventPublisher;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestCache;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.exceptions.LoginRequestNotFoundException;
import br.com.lwbaleeiro.cdauth.repository.LoginRequestRepository;
import br.com.lwbaleeiro.cdauth.service.DeviceService;
import br.com.lwbaleeiro.cdauth.service.LoginRequestCacheService;
import br.com.lwbaleeiro.cdauth.service.LoginRequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginRequestServiceImpl implements LoginRequestService {

    private final LoginRequestRepository loginRequestRepository;
    private final DeviceService deviceService;
    private final LoginRequestEventPublisher eventPublisher;
    private final LoginRequestCacheService cacheService;

    @Override
    public LoginRequest create(String deviceIdRequester, String deviceNameRequester) {

        LoginRequest loginRequest = LoginRequest
                .builder()
                .deviceIdRequester(deviceIdRequester)
                .deviceNameRequester(deviceNameRequester)
                .createdAt(Instant.now())
                .status(LoginRequestStatus.PENDING)
                .build();

        LoginRequest saved = loginRequestRepository.save(loginRequest);
        cacheService.cacheLoginRequest(loginRequest.getId(), loginRequest);
        return saved;
    }

    @Override
    public LoginRequest getLoginStatus(UUID id) {
        return cacheService.getLoginRequest(id).orElseGet(
                () ->  loginRequestRepository.findById(id).orElseThrow(
                        () -> new LoginRequestNotFoundException("No login request found by given id."))
        );
    }

    @Override
    public LoginRequest loginApprove(UUID id, String deviceIdApprove, User user) {

        LoginRequest loginRequest = loginRequestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Login request not found by given ID."));

        if (loginRequest.getStatus() != LoginRequestStatus.PENDING) {
            throw new IllegalStateException("LoginRequest is already resolved.");
        }

        loginRequest.setDeviceIdApprove(deviceIdApprove);
        loginRequest.setUser(user);
        loginRequest.setStatus(LoginRequestStatus.APPROVED);
        loginRequest.setApprovedAt(Instant.now());

       deviceService.create(loginRequest.getDeviceIdRequester(),
               loginRequest.getDeviceNameRequester(),
               user);

        LoginRequest saved = loginRequestRepository.save(loginRequest);
        eventPublisher.sendUpdate(saved.getId(), saved.getStatus());
        cacheService.cacheLoginRequest(loginRequest.getId(), loginRequest);

        return saved;
    }

    @Override
    public LoginRequest loginReject(UUID id, String deviceIdReject, User user) {

        LoginRequest loginRequest = loginRequestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Login request not found by given ID."));

        if (loginRequest.getStatus() != LoginRequestStatus.PENDING) {
            throw new IllegalStateException("LoginRequest is already resolved.");
        }

        loginRequest.setDeviceIdApprove(deviceIdReject);
        loginRequest.setUser(user);
        loginRequest.setStatus(LoginRequestStatus.REJECTED);
        loginRequest.setRejectedAt(Instant.now());

        LoginRequest saved = loginRequestRepository.save(loginRequest);
        eventPublisher.sendUpdate(saved.getId(), saved.getStatus());
        cacheService.cacheLoginRequest(loginRequest.getId(), loginRequest);

        return saved;
    }

    private LoginRequestCache mapToDTO(LoginRequest loginRequest) {
        return new LoginRequestCache(
                loginRequest.getId(),
                loginRequest.getStatus().name(),
                Instant.now().plus(Duration.ofMinutes(2)),
                loginRequest.getUser() != null ? loginRequest.getUser().getId().toString() : null
        );
    }
}
