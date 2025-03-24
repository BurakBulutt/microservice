package com.example.serviceusers.users.api;

import lombok.Data;

@Data
public class PageUtil {
    private final int number;
    private final int size;
    private final int totalElements;
    private final int totalPages;
}
