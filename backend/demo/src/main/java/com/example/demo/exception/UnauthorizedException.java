package com.example.demo.exception;

/**
 * Exception za neautorizirane zahtjeve (401)
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

