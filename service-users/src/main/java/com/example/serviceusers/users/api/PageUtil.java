package com.example.serviceusers.users.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class PageUtil {
    private final int number;
    private final int size;
    private final int totalElements;
    private final int totalPages;

    public PageUtil(int number, int size, int totalElements) {
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public static PageUtil unPaged() {
        return new PageUtil(0, 0,  0);
    }
}
