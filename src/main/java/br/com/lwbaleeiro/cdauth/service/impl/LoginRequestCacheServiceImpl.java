package br.com.lwbaleeiro.cdauth.service.impl;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestCache;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.service.LoginRequestCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginRequestCacheServiceImpl implements LoginRequestCacheService {

    private final RedisTemplate<String, LoginRequestCache> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(2);

    @Override
    public void cacheLoginRequest(UUID loginRequestId, LoginRequest loginRequest) {
        String key = "login_request: " + loginRequestId.toString();

        LoginRequestCache cache = new LoginRequestCache(
                loginRequest.getId(),
                loginRequest.getStatus().name(),
                Instant.now().plus(Duration.ofMinutes(2)),
                loginRequest.getUser() != null ? loginRequest.getUser().getId().toString() : null
        );

        redisTemplate.opsForValue().set(key, cache, TTL);
    }

    @Override
    public Optional<LoginRequestCache> getLoginRequest(UUID loginRequestId) {
        String key = "login_request:" + loginRequestId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void deleteLoginRequest(UUID loginRequestId) {
        String key = "login_request" + loginRequestId.toString();
        redisTemplate.delete(key);
    }
}
