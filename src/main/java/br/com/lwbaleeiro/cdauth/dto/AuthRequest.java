package br.com.lwbaleeiro.cdauth.dto;

public record AuthRequest(String email,
                   String password,
                   String name,
                   String deviceId) {
}
