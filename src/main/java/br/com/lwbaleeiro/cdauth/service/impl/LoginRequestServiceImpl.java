package br.com.lwbaleeiro.cdauth.service.impl;

import br.com.lwbaleeiro.cdauth.config.JwtService;
import br.com.lwbaleeiro.cdauth.controller.LoginRequestEventPublisher;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
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
    private final JwtService jwtService;

    @Override
    public LoginRequestResponse create(String deviceIdRequester, String deviceNameRequester) {
        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(Duration.ofMinutes(2));

        LoginRequest loginRequest = LoginRequest
                .builder()
                .deviceIdRequester(deviceIdRequester)
                .deviceNameRequester(deviceNameRequester)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .status(LoginRequestStatus.PENDING)
                .build();

        LoginRequest saved = loginRequestRepository.save(loginRequest);
        cacheService.cacheLoginRequest(loginRequest.getId(), loginRequest);
        return mapToDTO(saved, null);
    }

    @Override
    public LoginRequestResponse getLoginStatus(UUID id) {

        LoginRequestResponse loginRequestResponse = cacheService.getLoginRequest(id).orElseGet(
                () -> mapToDTO(loginRequestRepository.findById(id).orElseThrow(
                                () -> new LoginRequestNotFoundException("No login request found by given id.")),
                        null)
        );

        validateExpiration(loginRequestResponse.expiresAt());

        return loginRequestResponse;
    }

    @Override
    public LoginRequestResponse loginApprove(UUID id, String deviceIdApprove, User user) {

        LoginRequest loginRequest = loginRequestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Login request not found by given ID."));

        validateExpiration(loginRequest.getExpiresAt());

        if (loginRequest.getStatus() != LoginRequestStatus.PENDING) {
            throw new IllegalStateException("LoginRequest is already resolved.");
        }

       deviceService.getByDeviceIdAndUser(deviceIdApprove, user).orElseThrow(
               () -> new EntityNotFoundException("This device is not registered for the current user."));

        if (loginRequest.getUser() != null && !loginRequest.getUser().getId().equals(user.getId())) {
            //TODO: mudar exception
            throw new IllegalStateException("You cannot approve or reject login requests from another user.");
        }

        if (loginRequest.getDeviceIdRequester().equals(deviceIdApprove)) {
            //TODO: mudar exception
            throw new IllegalStateException("Cannot approve login from the same device that requested it.");
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

        String authToken = jwtService.generateToken(saved.getUser(),
                saved.getUser().getId(),
                saved.getDeviceIdRequester());

        return mapToDTO(saved, authToken);
    }

    @Override
    public LoginRequestResponse loginReject(UUID id, String deviceIdReject, User user) {

        LoginRequest loginRequest = loginRequestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Login request not found by given ID."));

        validateExpiration(loginRequest.getExpiresAt());

        if (loginRequest.getStatus() != LoginRequestStatus.PENDING) {
            throw new IllegalStateException("LoginRequest is already resolved.");
        }

        deviceService.getByDeviceIdAndUser(deviceIdReject, user).orElseThrow(
                () -> new EntityNotFoundException("This device is not registered for the current user."));

        if (loginRequest.getUser() != null && !loginRequest.getUser().getId().equals(user.getId())) {
            //TODO: mudar exception
            throw new IllegalStateException("You cannot approve or reject login requests from another user.");
        }

        loginRequest.setDeviceIdApprove(deviceIdReject);
        loginRequest.setUser(user);
        loginRequest.setStatus(LoginRequestStatus.REJECTED);
        loginRequest.setRejectedAt(Instant.now());

        LoginRequest saved = loginRequestRepository.save(loginRequest);
        eventPublisher.sendUpdate(saved.getId(), saved.getStatus());
        cacheService.cacheLoginRequest(loginRequest.getId(), loginRequest);

        return mapToDTO(saved, null);
    }

    private void validateExpiration(Instant expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new IllegalStateException("Login request has expired.");
        }
    }

    private LoginRequestResponse mapToDTO(LoginRequest loginRequest, String token) {
        return new LoginRequestResponse(
                loginRequest.getId(),
                loginRequest.getStatus().name(),
                Instant.now().plus(Duration.ofMinutes(2)),
                loginRequest.getUser() != null ? loginRequest.getUser().getId() : null,
                token
        );
    }
}
