package com.example.serviceauth.auth.keycloakutils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusGenerate {

    public static HttpStatus findByCode(int code) {
        switch (code) {
            case 400 -> {
                return HttpStatus.BAD_REQUEST;
            }
            case 401 -> {
                return HttpStatus.UNAUTHORIZED;
            }
            case 403 -> {
                return HttpStatus.FORBIDDEN;
            }
            case 404 -> {
                return HttpStatus.NOT_FOUND;
            }
            case 405 -> {
                return HttpStatus.METHOD_NOT_ALLOWED;
            }
            case 500 -> {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
            case 200 -> {
                return HttpStatus.OK;
            }
        }
        throw new RuntimeException("Status not found");
    }
}
