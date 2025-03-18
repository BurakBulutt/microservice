package com.example.serviceusers.rest;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final MessageResource messageResource;
    private final Object[] args;

    public BaseException(MessageResource messageResource,Object... args) {
        this.args = args;
        this.messageResource = messageResource;
    }
}
