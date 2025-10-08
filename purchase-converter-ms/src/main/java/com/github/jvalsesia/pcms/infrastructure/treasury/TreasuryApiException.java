package com.github.jvalsesia.pcms.infrastructure.treasury;

public class TreasuryApiException extends RuntimeException {
    public TreasuryApiException(String message) {
        super(message);
    }

    public TreasuryApiException(String message, Throwable cause) {
        super(message, cause);
    }
}