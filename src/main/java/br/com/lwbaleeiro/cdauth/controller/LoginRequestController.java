package br.com.lwbaleeiro.cdauth.controller;

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

import java.util.UUID;

@RestController
@RequestMapping("/api/login-request")
@RequiredArgsConstructor
public class LoginRequestController {

    private final LoginRequestService loginRequestService;
    private final AuthenticatedUserContext authenticatedUserContext;

    @PostMapping("/create")
    public ResponseEntity<LoginRequestResponse> loginRequest(@RequestBody LoginRequestRequest LoginRequest) {

        LoginRequest loginRequest = loginRequestService.create(LoginRequest.deviceIdRequester());
        return ResponseEntity.ok(new LoginRequestResponse(
                loginRequest.getId(), loginRequest.getStatus()));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<LoginRequestResponse> loginStatus(@PathVariable("id") String id) {

        LoginRequest loginRequest = loginRequestService.getLoginStatus(UUID.fromString(id));
        return ResponseEntity.ok(new LoginRequestResponse(loginRequest.getId(),
                loginRequest.getStatus()));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoginRequestResponse> loginApprove(@PathVariable("id") String id,
                                                             HttpServletRequest request) {

        User user = authenticatedUserContext.getAuthenticatedUser();
        String deviceIdApprove = authenticatedUserContext.getDeviceId(request);

        LoginRequest loginRequest = loginRequestService.loginApprove(UUID.fromString(id), deviceIdApprove, user);
        // TODO: Push com o token (user + deviceIdApprove) para authenticar o novo dispositivo
        
        return ResponseEntity.ok(new LoginRequestResponse(loginRequest.getId(), loginRequest.getStatus()));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoginRequestResponse> loginReject(@PathVariable("id") String id,
                                                            HttpServletRequest request) {

        User user = authenticatedUserContext.getAuthenticatedUser();
        String deviceIdReject = authenticatedUserContext.getDeviceId(request);

        LoginRequest loginRequest = loginRequestService.loginReject(UUID.fromString(id), deviceIdReject, user);
        return ResponseEntity.ok(new LoginRequestResponse(loginRequest.getId(), loginRequest.getStatus()));
    }
}
