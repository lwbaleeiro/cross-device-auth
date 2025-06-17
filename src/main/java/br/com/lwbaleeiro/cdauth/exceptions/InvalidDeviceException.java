package br.com.lwbaleeiro.cdauth.exceptions;

public class InvalidDeviceException extends RuntimeException {

    public InvalidDeviceException(String message) {
        super(message);
    }

    public InvalidDeviceException(String message, Throwable cause) {
        super(message, cause);
    }
}
