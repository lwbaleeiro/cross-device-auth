package br.com.lwbaleeiro.cdauth.service;

import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import br.com.lwbaleeiro.cdauth.repository.LoginRequestRepository;
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
}
