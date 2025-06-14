package br.com.lwbaleeiro.cdauth.controller;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestStatusResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoginRequestEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(UUID loginRequestId, LoginRequestStatus status) {
        messagingTemplate.convertAndSend("/topic/login-request/" + loginRequestId,
                new LoginRequestStatusResponse(loginRequestId, status));
    }
}
