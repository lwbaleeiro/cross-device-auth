package br.com.lwbaleeiro.cdauth.helpers;

import br.com.lwbaleeiro.cdauth.config.JwtService;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserContext {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("No User found by given email"));
    }

    public String getDeviceId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractDeviceId(token);
    }
}
