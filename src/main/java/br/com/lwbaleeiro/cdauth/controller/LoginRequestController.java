package br.com.lwbaleeiro.cdauth.controller;

import br.com.lwbaleeiro.cdauth.dto.LoginRequestRequest;
import br.com.lwbaleeiro.cdauth.dto.LoginRequestResponse;
import br.com.lwbaleeiro.cdauth.entity.LoginRequest;
import br.com.lwbaleeiro.cdauth.service.LoginRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/login-request")
@RequiredArgsConstructor
public class LoginRequestController {

    private final LoginRequestService loginRequestService;

    @PostMapping("/")
    public ResponseEntity<LoginRequestResponse> loginRequest(@RequestBody LoginRequestRequest LoginRequest) {

        LoginRequest loginRequest = loginRequestService.create(LoginRequest.deviceIdRequester());
        return ResponseEntity.ok(new LoginRequestResponse(
                loginRequest.getId(), loginRequest.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoginRequestResponse> loginStatus(@PathVariable("id") String id) {

        LoginRequest loginRequest = loginRequestService.getLoginStatus(UUID.fromString(id));
        return ResponseEntity.ok(new LoginRequestResponse(loginRequest.getId(),
                loginRequest.getStatus()));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoginRequestResponse> loginApprove(@PathVariable("id") String id) {
        //LoginRequest loginRequest = loginRequestService.loginApprove(UUID.fromString(id));
        return ResponseEntity.ok(new LoginRequestResponse(null, null));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoginRequestResponse> loginReject(@PathVariable("id") String id) {

        return ResponseEntity.ok(new LoginRequestResponse(null, null));
    }
}
