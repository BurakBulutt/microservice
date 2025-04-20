package com.example.serviceusers.users.api;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Page<T> {
    private final List<T> content;
    private final PageUtil page;

    public Page(List<T> content,PageUtil page) {
        this.content = content;
        this.page = page;
    }

    public <U> Page<U> map(Function<? super T,? extends U> mapper) {
        return new Page<>(this.content.stream().map(mapper).collect(Collectors.toList()),this.page);
    }

    public static <T> Page<T> emptyPage() {
        return new Page<>(Collections.emptyList(), PageUtil.unPaged());
    }
}
