package br.com.lwbaleeiro.cdauth.dto;

import br.com.lwbaleeiro.cdauth.entity.LoginRequestStatus;

import java.util.UUID;

public record LoginRequestResponse(UUID idLoginRequest,
                                   LoginRequestStatus status) {
}
