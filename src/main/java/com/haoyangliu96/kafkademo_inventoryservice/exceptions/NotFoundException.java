package com.haoyangliu96.kafkademo_inventoryservice.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
