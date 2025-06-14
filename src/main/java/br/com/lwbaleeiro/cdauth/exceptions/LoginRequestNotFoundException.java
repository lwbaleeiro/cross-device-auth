package br.com.lwbaleeiro.cdauth.exceptions;

public class LoginRequestNotFoundException extends RuntimeException {

    public LoginRequestNotFoundException(String message) {
        super(message);
    }

    public LoginRequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
