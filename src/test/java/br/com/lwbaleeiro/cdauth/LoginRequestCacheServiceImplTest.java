package br.com.lwbaleeiro.cdauth;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.service.impl.LoginRequestCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginRequestCacheServiceImplTest  {

    @Mock
    private RedisTemplate<String, LoginRequestResponse> redisTemplate;

    @Mock
    private ValueOperations<String, LoginRequestResponse> valueOperations;

    private LoginRequestCacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new LoginRequestCacheServiceImpl(redisTemplate);
    }

    @Test
    void shouldCacheLoginRequest() {
        UUID requestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setId(requestId);
        loginRequest.setStatus(LoginRequestStatus.PENDING);
        loginRequest.setUser(user);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheService.cacheLoginRequest(requestId, loginRequest);

        ArgumentCaptor<LoginRequestResponse> captor = ArgumentCaptor.forClass(LoginRequestResponse.class);
        verify(valueOperations).set(eq("login_request: " + requestId.toString()), captor.capture(), eq(Duration.ofMinutes(2)));

        LoginRequestResponse cached = captor.getValue();
        assertEquals(requestId, cached.id());
        assertEquals(LoginRequestStatus.PENDING.name(), cached.status());
        assertEquals(userId, cached.userId());
        assertNotNull(cached.expiresAt());
    }

    @Test
    void shouldGetLoginRequest() {
        UUID requestId = UUID.randomUUID();
        LoginRequestResponse response = new LoginRequestResponse(
                requestId,
                LoginRequestStatus.PENDING.name(),
                Instant.now().plus(Duration.ofMinutes(2)),
                UUID.randomUUID(),
                null
        );

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login_request:" + requestId)).thenReturn(response);

        Optional<LoginRequestResponse> result = cacheService.getLoginRequest(requestId);
        assertTrue(result.isPresent());
        assertEquals(response, result.get());
    }

    @Test
    void shouldDeleteLoginRequest() {
        UUID requestId = UUID.randomUUID();
        cacheService.deleteLoginRequest(requestId);
        verify(redisTemplate).delete("login_request" + requestId.toString());
    }
}
