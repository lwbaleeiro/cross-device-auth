package br.com.lwbaleeiro.cdauth.controller;

import br.com.lwbaleeiro.cdauth.config.JwtService;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestRequest;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.entity.User;
import br.com.lwbaleeiro.cdauth.helpers.AuthenticatedUserContext;
import br.com.lwbaleeiro.cdauth.service.LoginRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/login-request")
@RequiredArgsConstructor
public class LoginRequestController {

    private final LoginRequestService loginRequestService;
    private final AuthenticatedUserContext authenticatedUserContext;

    @PostMapping("/create")
    public ResponseEntity<LoginRequestResponse> loginRequest(@RequestBody LoginRequestRequest request) {

        LoginRequestResponse loginRequestResponse = loginRequestService.create(
                request.deviceIdRequester(), request.deviceNameRequester()
        );

        return ResponseEntity.ok(loginRequestResponse);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<LoginRequestResponse> loginStatus(@PathVariable("id") String id) {

        LoginRequestResponse loginRequestResponse = loginRequestService.getLoginStatus(UUID.fromString(id));
        return ResponseEntity.ok(loginRequestResponse);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoginRequestResponse> loginApprove(@PathVariable("id") String id,
                                                             HttpServletRequest request) {

        User user = authenticatedUserContext.getAuthenticatedUser();
        String deviceIdApprove = authenticatedUserContext.getDeviceId(request);

        LoginRequestResponse loginRequestResponse = loginRequestService.loginApprove(
                UUID.fromString(id), deviceIdApprove, user
        );
        
        return ResponseEntity.ok(loginRequestResponse);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoginRequestResponse> loginReject(@PathVariable("id") String id,
                                                            HttpServletRequest request) {

        User user = authenticatedUserContext.getAuthenticatedUser();
        String deviceIdReject = authenticatedUserContext.getDeviceId(request);

        LoginRequestResponse loginRequestResponse = loginRequestService.loginReject(
                UUID.fromString(id), deviceIdReject, user
        );

        return ResponseEntity.ok(loginRequestResponse);
    }
}
