package com.example.serviceusers.users.api;

import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Page<T> {
    private final int number;
    private final int size;
    private final int totalElements;
    private final int numberOfElements;
    private final List<T> content;


    @Deprecated
    public static <U,R> Page<U> map(Page<R> page, Function<R, U> mapper) {
        return new Page<>(page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getNumberOfElements(),
                page.getContent().stream().map(mapper).toList());
    }


    public <U> Page<U> map(Function<? super T,? extends U> mapper) {
        return new Page<>(this.number,this.size,this.totalElements,this.numberOfElements,this.content.stream().map(mapper).collect(Collectors.toList()));
    }
}
