//package br.com.lwbaleeiro.cdauth;
//
//import br.com.lwbaleeiro.cdauth.controller.LoginRequestEventPublisher;
//import br.com.lwbaleeiro.cdauth.dto.LoginRequestStatusResponse;
//import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.*;
//import org.springframework.web.socket.WebSocketHttpHeaders;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//
//import java.lang.reflect.Type;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class LoginRequestWebSocketIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    private WebSocketStompClient stompClient;
//    private String wsUrl;
//
//    @Autowired
//    private LoginRequestEventPublisher eventPublisher;
//
//    @BeforeAll
//    void setup() {
//        wsUrl = "ws://localhost:" + port + "ws/login-request"; //
//
//        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//    }
//
//    @Test
//    void clientShouldReceiveWebSocketMessageOnLoginRequestTopic() throws Exception {
////        UUID loginRequestId = UUID.randomUUID();
////        LoginRequestStatus status = LoginRequestStatus.APPROVED;
////
////        CompletableFuture<LoginRequestStatusResponse> future = new CompletableFuture<>();
////
////        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
////        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders();
////        StompHeaders connectHeaders = new StompHeaders();
////
////        StompSession session = stompClient.connectAsync(
////                wsUrl,
////                wsHeaders,
////                connectHeaders,
////                sessionHandler
////        ).get(1, TimeUnit.SECONDS);
////
////
////
////        session.subscribe("/topic/login-request/" + loginRequestId, new StompFrameHandler() {
////            @Override
////            public Type getPayloadType(StompHeaders headers) {
////                return LoginRequestStatusResponse.class;
////            }
////
////            @Override
////            public void handleFrame(StompHeaders headers, Object payload) {
////                future.complete((LoginRequestStatusResponse) payload);
////            }
////        });
////
////        // Aguarda conexão antes de enviar
////        Thread.sleep(500);
////
////        eventPublisher.sendUpdate(loginRequestId, status);
////
////        // Aguarda até 2 segundos pela mensagem
////        LoginRequestStatusResponse received = future.get(2, TimeUnit.SECONDS);
////
////        assertNotNull(received);
////        assertEquals(loginRequestId, received.requestId());
////        assertEquals(status, received.status());
////    }
//}
