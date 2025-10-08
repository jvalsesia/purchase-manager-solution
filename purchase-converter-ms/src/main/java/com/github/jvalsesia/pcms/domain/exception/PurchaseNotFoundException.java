package com.github.jvalsesia.pcms.domain.exception;

import java.util.UUID;

public class PurchaseNotFoundException extends RuntimeException {
    public PurchaseNotFoundException(UUID id) {
        super(String.format("Purchase with id %s not found", id));
    }
}