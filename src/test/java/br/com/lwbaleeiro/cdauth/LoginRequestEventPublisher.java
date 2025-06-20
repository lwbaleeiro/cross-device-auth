package br.com.lwbaleeiro.cdauth;

import br.com.lwbaleeiro.cdauth.controller.LoginRequestEventPublisher;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestStatusResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginRequestEventPublisherTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LoginRequestEventPublisher eventPublisher;

    @Test
    void shouldSendUpdateToCorrectWebSocketTopic() {
        // Arrange
        UUID loginRequestId = UUID.randomUUID();
        LoginRequestStatus status = LoginRequestStatus.APPROVED;

        LoginRequestStatusResponse expectedMessage =
                new LoginRequestStatusResponse(loginRequestId, status);

        String expectedDestination = "/topic/login-request/" + loginRequestId;

        // Act
        eventPublisher.sendUpdate(loginRequestId, status);

        // Assert
        verify(messagingTemplate).convertAndSend(expectedDestination, expectedMessage);
    }
}
