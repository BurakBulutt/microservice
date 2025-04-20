package com.example.serviceusers.users.api;


import lombok.Data;


@Data
public class PageUtil {
    private final int number;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageUtil(int number,int size,long totalElements) {
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public static PageUtil unPaged() {
        return new PageUtil(0, 0,  0);
    }
}
