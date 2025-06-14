package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.exceptions.LoginRequestNotFoundException;
import br.com.lwbaleeiro.cdauth.repository.LoginRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginRequestServiceImpl implements LoginRequestService {

    private final LoginRequestRepository loginRequestRepository;
    private final DeviceService deviceService;

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
        return loginRequestRepository.findById(id).orElseThrow(
                () -> new LoginRequestNotFoundException("No login request found by given id."));
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

        return loginRequestRepository.save(loginRequest);
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

        return loginRequestRepository.save(loginRequest);
    }
}
